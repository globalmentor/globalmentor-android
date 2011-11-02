/*
 * Copyright © 2011 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.android.widget;

import static com.google.common.base.Preconditions.*;
import static java.util.Collections.*;

import java.util.*;

import com.globalmentor.android.view.Axis;

import android.content.Context;
import android.graphics.*;
import android.view.*;

/**
 * A view that presents images as a series of slides.
 * <p>
 * This view is light-weight and concentrates on UI-specific functionality such as flings and convenience transition methods. More complex business logic such
 * as image loading and automatic transitions should be performed by consumers of this view.
 * </p>
 * 
 * <p>
 * The fling response logic here was first based upon <a
 * href="http://mobile.tutsplus.com/tutorials/android/android-gesture/">Android SDK: Introduction to Gestures</a>.
 * </p>
 * 
 * @author Garret Wilson
 * 
 * @see <a href="http://mobile.tutsplus.com/tutorials/android/android-gesture/">Android SDK: Introduction to Gestures</a>
 * @see <a href="http://www.codeshogun.com/blog/2009/04/16/how-to-implement-swipe-action-in-android/">How to implement Swipe action in Android</a>
 * @see <a href="http://stackoverflow.com/questions/937313/android-basic-gesture-detection">Android - basic gesture detection</a>
 */
public class ImageSlideView extends View
{

	/** The axis on which sliding should occur. */
	private Axis slideAxis;

	/** @return The axis on which sliding should occur. */
	public Axis getSlideAxis()
	{
		return slideAxis;
	}

	/**
	 * Sets the axis on which sliding should occur.
	 * @param slideAxis The axis on which sliding should occur.
	 * @throws NullPointerException if the given axis is <code>null</code>.
	 */
	public void setSlideAxis(final Axis slideAxis)
	{
		this.slideAxis = checkNotNull(slideAxis);
		gestureListener.setAxes(slideAxis); //inform the gesture listener of the new slide axis
	}

	/** Our custom listener responding to gestures. */
	private final GestureListener gestureListener;

	/** The gesture detector. */
	private final GestureDetector gestureDetector;

	/** The matrix indicating the at-rest scaling and position of the current image. */
	private Matrix originMatrix = new Matrix();

	/** The matrix indicating the current position of the image. */
	private Matrix currentMatrix = new Matrix();

	/**
	 * The matrix indicating the last known position scrolled by the user. This position is only updated by user scroll---it is not updated by resetting the image
	 * location. This record is necessary to detect both scroll ending and fling start location.
	 */
	private Matrix scrollMatrix = new Matrix();

	/** The list of images to be displayed; this list will be completely replaced when new images are provided. */
	private List<Bitmap> images = emptyList();

	/**
	 * The index of the image being displayed in the images list. Because of race conditions, this variable may not always indicate a valid index in the images
	 * list. Rather than synchronizing access to related variables, code using this index should compensate for invalid indexes by choosing another image (or
	 * none) as appropriate.
	 */
	private int imageIndex = -1;

	/** @return The image currently being displayed, or <code>null</code> if no image is being displayed. */
	public Bitmap getImage()
	{
		final List<Bitmap> images = this.images; //get a reference to the list to prevent race conditions
		int imageIndex = this.imageIndex; //get a copy of the image index because we may choose another image
		final int imageCount = images.size(); //find out how many images there are
		if(imageCount == 0) //if there are no images
		{
			return null; //no image can be selected
		}
		if(imageIndex < 0 || imageIndex >= imageCount) //if an invalid index is indicated
		{
			imageIndex = 0; //assume the first image
		}
		return images.get(imageIndex); //return the selected image
	}

	/**
	 * Sets the images to be shown and selects the first one, if any.
	 * @param images The images to be displayed.
	 */
	public void setImages(final List<Bitmap> images)
	{
		setImages(images, 0);
	}

	/**
	 * Sets the images to be shown and selects one of them.
	 * @param images The images to be displayed.
	 * @param index The index of the image to show immediately.
	 * @throws IndexOutOfBoundsException if the given index refers to a location not in the collection
	 */
	public void setImages(final List<Bitmap> images, final int index)
	{
		checkElementIndex(index, images.size());
		this.images = new ArrayList<Bitmap>(images);
		goImage(index); //go to the indicated image, which resets the position matrixes appropriately
	}

	/**
	 * Changes to the image at the given index. Sizes and positions are reset to match the indicated image.
	 * <p>
	 * All image changes should eventually call this method.
	 * </p>
	 * @param index The index of the image to which to change.
	 * @see #recalculateImage()
	 */
	public void goImage(final int index)
	{
		this.imageIndex = index; //change the index
		recalculateImage(); //recalculate the image size and position
	}

	/**
	 * Changes to previous image. If there is no previous image, the last image will be shown instead.
	 */
	public void goPreviousImage()
	{
		goImage(imageIndex > 0 ? imageIndex - 1 : images.size() - 1); //go to the previous image, wrapping around if needed
	}

	/**
	 * Changes to next image. If there is no next image, the first image will be shown instead.
	 */
	public void goNextImage()
	{
		goImage(imageIndex < images.size() - 1 ? imageIndex + 1 : 0); //go to the next image, wrapping around if needed
	}

	/**
	 * Recalculates the current image and then resets the image position.
	 * <p>
	 * This method is usually called when the image is changed or the view is resized.
	 * </p>
	 * @see #resetImagePosition()
	 */
	protected void recalculateImage()
	{
		originMatrix.reset(); //reset the image matrix
		final float viewWidth = getWidth(); //get the current view dimensions; use floats because we'll be doing calculations
		final float viewHeight = getHeight();
		if(viewWidth > 0 && viewHeight > 0) //if the view has been resized
		{
			final Bitmap image = getImage(); //get the current image, if any
			if(image != null) //if we are showing an image
			{
				final float imageWidth = image.getWidth();
				final float imageHeight = image.getHeight();
				final float widthRatio = imageWidth / viewWidth;
				final float heightRatio = imageHeight / viewHeight;
				final float scaleRatio = 1 / Math.max(heightRatio, widthRatio);
				originMatrix.preScale(scaleRatio, scaleRatio); //scale the image to fit inside the view
				final float newImageWidth = image.getWidth() * scaleRatio;
				final float newImageHeight = image.getHeight() * scaleRatio;
				final float centerXTranslate = (viewWidth - newImageWidth) / 2; //center the image if needed
				final float centerYTranslate = (viewHeight - newImageHeight) / 2;
				originMatrix.postTranslate(centerXTranslate, centerYTranslate);
			}
		}
		resetImagePosition(); //reset the image position
	}

	/** Resets the current image by rescaling and repositioning the image to its initial location. */
	protected void resetImagePosition()
	{
		currentMatrix.set(originMatrix); //reset the translate matrix to the initial position
		invalidate(); //invalidate the view so that the image will be shown/repainted
	}

	public ImageSlideView(Context context)
	{
		super(context);
		slideAxis = Axis.HORIZONTAL; //default to sliding horizontally
		gestureListener = new GestureListener(context);
		gestureListener.setAxes(slideAxis); //initialize the gesture listener to detect flings on the same axis we handle
		gestureDetector = new GestureDetector(context, gestureListener);
		setOnTouchListener(gestureListener); //listen for touches so we will know when scrolling is finished
	}

	/**
	 * {@inheritDoc} This version recalculates the position and size of the image to fit the new view size.
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		recalculateImage(); //recalculate the size and position of the image
	}

	/**
	 * Moves the relative position of the image.
	 * @param dx The relative horizontal position of the image.
	 * @param dy The relative vertical position of the image.
	 */
	public void moveImage(float dx, float dy)
	{
		currentMatrix.postTranslate(dx, dy);
		invalidate();
	}

	/**
	 * Slides the image off the screen in a given direction at the given velocity. After sliding, the next or previous image will be displayed.
	 * <p>
	 * Sliding occurs from the current position matrix.
	 * </p>
	 * @param velocityX The horizontal velocity, or 0 if no sliding should occur horizontally.
	 * @param velocityY The vertical velocity, or 0 if no sliding should occur vertically.
	 * @see #getSlideAxis()
	 */
	public void slideImage(final float velocityX, final float velocityY)
	{
		final int viewWidth = getWidth();
		final int viewHeight = getHeight();
		final Matrix startMatrix = new Matrix(currentMatrix); //start sliding from our current position
		final float[] startValues = new float[9];
		startMatrix.getValues(startValues); //get the start position values of the image
		final float startX = startValues[Matrix.MTRANS_X];
		final float startY = startValues[Matrix.MTRANS_Y];
		final float[] originValues = new float[9];
		originMatrix.getValues(originValues); //get the original position values of the image
		final float endX; //determine where we should end, based upon the velocity directions
		if(velocityX < 0) //if we are sliding left
		{
			endX = originValues[Matrix.MTRANS_X] - viewWidth; //we'll slide a whole view left
		}
		else if(velocityX > 0) //if we are sliding right
		{
			endX = originValues[Matrix.MTRANS_X] + viewWidth; //we'll slide a whole view right
		}
		else
		//if we are not sliding horizontally
		{
			endX = startX; //no need to slide at all
		}
		final float endY; //determine where we should end, based upon the velocity directions
		if(velocityY < 0) //if we are sliding up
		{
			endY = originValues[Matrix.MTRANS_Y] - viewHeight; //we'll slide a whole view up
		}
		else if(velocityY > 0) //if we are sliding down
		{
			endY = originValues[Matrix.MTRANS_Y] + viewHeight; //we'll slide a whole view down
		}
		else
		//if we are not sliding vertically
		{
			endY = startY; //no need to slide at all
		}
		post(new Runnable() //post an event to start sliding
		{
			@Override
			public void run()
			{
				slideAnimateStep(System.currentTimeMillis(), startMatrix, startX, startY, endX, endY, velocityX, velocityY);
			}
		});
	}

	/**
	 * Performs a single step in a slide animation.
	 * @param startTime The time the animation started.
	 * @param startMatrix The matrix indicating the start position of the image.
	 * @param startX The horizontal slide starting position.
	 * @param startY The vertical slide starting position.
	 * @param endX The horizontal slide ending position.
	 * @param endY The vertical slide ending position.
	 * @param velocityX The horizontal velocity, or 0 if no sliding should occur horizontally.
	 * @param velocityY The vertical velocity, or 0 if no sliding should occur vertically.
	 */
	private void slideAnimateStep(final long startTime, final Matrix startMatrix, final float startX, final float startY, final float endX, final float endY,
			final float velocityX, final float velocityY)
	{
		final long currentTime = System.currentTimeMillis();
		final long timePassed = currentTime - startTime; //see how much time has passed since we started
		final float distanceX = timePassed * (velocityX / 1000); //see how much distance we should cover in that time
		final float distanceY = timePassed * (velocityY / 1000); //see how much distance we should cover in that time
		currentMatrix.set(startMatrix); //reset the current position back to the starting position
		moveImage(distanceX, 0); //translate the image to its current animation position
		if(startX + distanceX < endX || startY + distanceY < endY) //if we haven't finished the animation
		{
			final long delayX = (long)(1000 / velocityX); //see how long we should delay before a single pixel would be traversed at the requested velocity;
			final long delayY = (long)(1000 / velocityY); //there is no need to continue animating until that point
			postDelayed(new Runnable() //post another animation step
					{
						@Override
						public void run()
						{
							slideAnimateStep(startTime, startMatrix, startX, startY, endX, endY, velocityX, velocityY);
						}
					}, Math.min(delayX, delayY)); //sleep until it's time for another animation step
		}
		else
		//when animation is finished
		{
			final float velocity1, velocity2; //give precedence to the appropriate axis
			switch(getSlideAxis())
			{
				case HORIZONTAL:
					velocity1 = velocityX;
					velocity2 = velocityY;
					break;
				case VERTICAL:
					velocity1 = velocityY;
					velocity2 = velocityX;
					break;
				default:
					throw new AssertionError("Unrecognized axis: " + getSlideAxis());
			}
			if(velocity1 < 0 || velocity2 < 0) //if sliding backwards
			{
				goNextImage(); //go to the next image
			}
			else if(velocity1 > 0 || velocity2 > 0) //if sliding forwards
			{
				goPreviousImage(); //go to the previous image
			}
		}
	}

	/**
	 * {@inheritDoc} This version delegates touch event handling to the gesture detector.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return gestureDetector.onTouchEvent(event);
	}

	/**
	 * {@inheritDoc} This version draws the image as appropriate.
	 */
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		final Bitmap currentImage = getImage(); //get our current image, if any 
		if(currentImage != null) //if we have an image
		{
			canvas.drawBitmap(currentImage, currentMatrix, null); //draw the image using our current scale/translate matrix
		}
	}

	/**
	 * A gesture listener that handles scrolling and flinging.
	 * 
	 * @author Garret Wilson
	 * 
	 * @see <a href="http://www.codeshogun.com/blog/2009/04/16/how-to-implement-swipe-action-in-android/">How to implement Swipe action in Android</a>
	 * @see <a href="http://stackoverflow.com/questions/937313/android-basic-gesture-detection">Android - basic gesture detection</a>
	 * @see <a href="http://stackoverflow.com/questions/2089552/android-how-to-detect-when-a-scroll-has-ended">Android: How to detect when a scroll has ended</a>
	 */
	private class GestureListener extends PageFlingListener implements OnTouchListener
	{

		/** Whether scrolling is currently occurring. */
		private boolean isScrolling = false;

		/**
		 * Context constructor.
		 * @param context The current context.
		 * @throws NullPointerException if the given context is <code>null</code>.
		 */
		public GestureListener(final Context context)
		{
			super(context);
		}

		/**
		 * {@inheritDoc} This version returns <code>true</code> so that scolling events will be caught.
		 */
		@Override
		public boolean onDown(MotionEvent e)
		{
			return true;
		}

		/**
		 * {@inheritDoc} This version moves the image on the slide axis in response to a user scroll.
		 */
		@Override
		public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY)
		{
			isScrolling = true; //make a note that scrolling has started
			final Axis slideAxis = getSlideAxis();
			moveImage(slideAxis == Axis.HORIZONTAL ? -distanceX : 0, slideAxis == Axis.VERTICAL ? -distanceY : 0); //move the image, but only along the sliding axis
			scrollMatrix.set(currentMatrix); //make a note of the new scroll position, in case we need it for a fling
			return true;
		}

		/**
		 * {@inheritDoc} This version slides the image in the appropriate direction.
		 */
		@Override
		public boolean onVerifiedFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			currentMatrix.set(scrollMatrix); //reset the image back to the location of the last scroll; otherwise the touch-up will reset the image 
			invalidate(); //invalidate the view so that the image will be shown/repainted
			slideImage(velocityX, velocityY); //slide the image at the appropriate velocity
			return true;
		}

		/**
		 * {@inheritDoc} This version listens for touch ending so that the image can be reset when scrolling is finished (although a fling may later override this).
		 * @see <a href="http://stackoverflow.com/questions/2089552/android-how-to-detect-when-a-scroll-has-ended">Android: How to detect when a scroll has
		 *      ended</a>
		 */
		@Override
		public boolean onTouch(final View v, final MotionEvent event)
		{
			if(onTouchEvent(event)) //process the touch event normally if needed
			{
				return true;
			}
			if(event.getAction() == MotionEvent.ACTION_UP) //if this is a touch up
			{
				if(isScrolling) //if we had been scrolling
				{
					isScrolling = false; //scrolling has now ended
					resetImagePosition(); //reset the image to its original position
				}
			}
			return false;
		}
	}

}
