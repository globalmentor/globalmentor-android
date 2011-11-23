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

package com.globalmentor.model;

import static com.google.common.base.Preconditions.*;

import java.util.*;

/**
 * Manages a sequence of several states, allowing a consumer to poll for states, with state transitions occurring when needed. For example, this manager may
 * keep track of a number of image transition delays, increasing the delay after some amount of time has passed.
 * 
 * <p>
 * For each state, a minimum duration and a minimum number of polls may be supplied. A state will only transition after the minimum duration has passed
 * <em>and</em> the requested minimum number of polls has occurred. After the maximum duration or maximum poll count is reached, a transition always occurs.
 * Once the last state is reached, the state will not transition regardless of duration or polls.
 * </p>
 * 
 * <p>
 * The same state may occur several times in the sequence.
 * </p>
 * 
 * <p>
 * State transition does not occur asynchronously; the current state will be determined only when queried.
 * </p>
 * 
 * <p>
 * If there is at least one state defined, {@link #getState()} and {@link #pollState()} will never return <code>null</code>.
 * </p>
 * 
 * <p>
 * This class is thread-safe.
 * </p>
 * 
 * @param <S> The type of state being managed.
 * 
 * @author Garret Wilson
 * 
 */
public class PollStateManager<S> //TODO move to general utility class
{
	/** The ordered list of states and corresponding info. */
	private final List<StateInfo> stateInfos = new ArrayList<StateInfo>();

	/** The index into the current list of states. */
	private int stateInfoIndex = 0;

	/** Default constructor. */
	public PollStateManager()
	{
	}

	/**
	 * Adds a new state at the end of the sequence of states being managed, with a maximum duration before transition.
	 * @param state The state.
	 * @param maxDuration The maximum duration, in milliseconds, for a transition, regardless of polls.
	 * @throws NullPointerException if the given state is <code>null</code>.
	 */
	public synchronized void addMaxDurationState(final S state, final long maxDuration)
	{
		addState(state, maxDuration, Long.MAX_VALUE);
	}

	/**
	 * Adds a new state at the end of the sequence of states being managed, with a maximum poll count before transition.
	 * @param state The state.
	 * @param maxPollCount The maximum number of polls before a transition, regardless of duration.
	 * @throws NullPointerException if the given state is <code>null</code>.
	 */
	public synchronized void addMaxPollCountState(final S state, final long maxPollCount)
	{
		addState(state, Long.MAX_VALUE, maxPollCount);
	}

	/**
	 * Adds a new state at the end of the sequence of states being managed. Either the maximum duration or maximum poll count will cause the state to transition.
	 * @param state The state.
	 * @param maxDuration The maximum duration, in milliseconds, for a transition, regardless of polls.
	 * @param maxPollCount The maximum number of polls before a transition, regardless of duration.
	 * @throws NullPointerException if the given state is <code>null</code>.
	 */
	public synchronized void addState(final S state, final long maxDuration, final long maxPollCount)
	{
		addState(state, Long.MAX_VALUE, maxDuration, Long.MAX_VALUE, maxPollCount);
	}

	/**
	 * Adds a new state at the end of the sequence of states being managed.
	 * @param state The state.
	 * @param minDuration The minimum duration, in milliseconds, before a transition.
	 * @param maxDuration The maximum duration, in milliseconds, for a transition, regardless of polls.
	 * @param minPollCount The minimum number of polls before a transition.
	 * @param maxPollCount The maximum number of polls before a transition, regardless of duration.
	 * @throws NullPointerException if the given state is <code>null</code>.
	 */
	public synchronized void addState(final S state, final long minDuration, final long maxDuration, final long minPollCount, final long maxPollCount)
	{
		stateInfos.add(new StateInfo(state, minDuration, maxDuration, minPollCount, maxPollCount));
	}

	/** @return Info for the current state, or <code>null</code> if no states are defined. */
	protected synchronized StateInfo getStateInfo()
	{
		return !stateInfos.isEmpty() ? stateInfos.get(stateInfoIndex) : null;
	}

	/**
	 * Determines whether there exists states after the current state, if any.
	 * @return <code>true</code> if there exists states after the current state, if any, or <code>false</code> if there are no further states or no states are
	 *         defined.
	 */
	public synchronized boolean hasNextState()
	{
		return stateInfoIndex < stateInfos.size() - 1;
	}

	/**
	 * Determines the amount of time the current state has been in effect. This method does not transition the state, so the returned duration may be longer than
	 * the maximum duration for the current state.
	 * @return The amount of time, in milliseconds, the current state has been in effect, or -1 if no states are defined.
	 */
	public synchronized long getStateDuration()
	{
		final StateInfo stateInfo = getStateInfo();
		return stateInfo != null ? stateInfo.getDuration() : -1;
	}

	/**
	 * Determines the amount of time the current state has remaining to be in effect before its maximum duration. This value may be negative if the state has been
	 * in effect longer than its maximum allowed.
	 * @return The amount of time, in milliseconds, the current state has remaining to be in effect, or {@link Long#MAX_VALUE} if no states are defined.
	 */
	public long getStateRemainingDuration()
	{
		final StateInfo stateInfo = getStateInfo();
		return stateInfo != null ? stateInfo.getRemainingDuration() : Long.MAX_VALUE;
	}

	/**
	 * Retrieves the current state. The current poll count is not affected, but if sufficient time has passed the state may first transition because of duration.
	 * @return The current state, or <code>null</code> if no states are defined.
	 */
	public synchronized S getState()
	{
		StateInfo stateInfo = getStateInfo();
		while(stateInfo != null && isTransitionReady()) //if we should transition states
		{
			stateInfo = transition(); //transition the state
		}
		return stateInfo != null ? stateInfo.getState() : null;
	}

	/**
	 * Polls and then retrieves the current state. The state is transitioned if the increased poll count or duration requires.
	 * @return The current state, or <code>null</code> if no states are defined.
	 */
	public synchronized S pollState()
	{
		StateInfo stateInfo = getStateInfo();
		if(stateInfo != null)
		{
			stateInfo.poll(); //poll the state
		}
		return getState();
	}

	/**
	 * Determines if a transition should occur based upon the current duration and poll count of the current state. Specifically, transition should occur if
	 * either the duration or the poll count is above the current maximum, or both are above their minimums.
	 * @return <code>true</code> if this state should transition, or <code>null</code> if there are no states or the current state is the last state.
	 * @see #hasNextState()
	 */
	public synchronized boolean isTransitionReady()
	{
		final StateInfo stateInfo = getStateInfo();
		return stateInfo != null && hasNextState() && stateInfo.isTransitionReady();
	}

	/**
	 * Transitions to the next state in the sequence. The new state is reset. If there are no further states in the sequence, no action occurs.
	 * @return Information on the new state, or <code>null</code> if no states are defined.
	 */
	protected synchronized StateInfo transition()
	{
		final StateInfo stateInfo;
		if(stateInfoIndex < stateInfos.size() - 1) //if there are more states in the sequence
		{
			stateInfoIndex++; //go to the next state
			stateInfo = getStateInfo(); //get the new state information
			if(stateInfo != null) //if we have a new state
			{
				stateInfo.reset(); //reset the new state
			}
		}
		else
		//if there are no more states
		{
			stateInfo = getStateInfo(); //the state hasn't changed
		}
		return stateInfo;
	}

	/**
	 * Resets the poll manager to the first state in the sequence. That state's information will be reset.
	 * @return The new state, or <code>null</code> if no states are defined.
	 * @see #resetState()
	 */
	public synchronized S reset()
	{
		stateInfoIndex = 0; //reset the state index
		return resetState();
	}

	/**
	 * Resets the information for the current state by setting the poll count to zero and initializing the time to the current time. If no states are defined, no
	 * action occurs.
	 * @return The current state, or <code>null</code> if no states are defined.
	 */
	public synchronized S resetState()
	{
		final StateInfo stateInfo = getStateInfo();
		if(stateInfo != null)
		{
			stateInfo.reset();
			return stateInfo.getState();
		}
		else
		{
			return null;
		}
	}

	/**
	 * The value class for keeping track of the current state, along with transition requirements and current duration and poll count.
	 * @author Garret Wilson
	 */
	private class StateInfo
	{
		/** The state. */
		private final S state;

		/** @return The state. */
		public S getState()
		{
			return state;
		}

		/** The minimum duration, in milliseconds, before a transition. */
		private final long minDuration;

		/** @return The minimum duration, in milliseconds, before a transition. */
		public long getMinDuration()
		{
			return minDuration;
		}

		/** The maximum duration, in milliseconds, for a transition, regardless of polls. */
		private final long maxDuration;

		/** @return The maximum duration, in milliseconds, for a transition, regardless of polls. */
		public long getMaxDuration()
		{
			return maxDuration;
		}

		/** The minimum number of polls before a transition. */
		private final long minPollCount;

		/** @return The minimum number of polls before a transition. */
		public long getMinPollCount()
		{
			return minPollCount;
		}

		/** The maximum number of polls before a transition, regardless of duration. */
		private final long maxPollCount;

		/** @return The maximum number of polls before a transition, regardless of duration. */
		public long getMaxPollCount()
		{
			return maxPollCount;
		}

		/**
		 * The time this state was started.
		 * @see System#currentTimeMillis()
		 */
		private long startTime;

		/** The number of polls this state has experienced. */
		private long pollCount;

		/** Increments the poll count. */
		public void poll()
		{
			pollCount++;
		}

		/**
		 * Determines the amount of time the current state has been in effect.
		 * @return The amount of time, in milliseconds, the current state has been in effect.
		 */
		public long getDuration()
		{
			return System.currentTimeMillis() - startTime;
		}

		/**
		 * Determines the amount of time the current state has remaining to be in effect before its maximum duration. This value may be negative if the state has
		 * been in effect longer than its maximum allowed.
		 * @return The amount of time, in milliseconds, the current state has remaining to be in effect.
		 * @see #getMaxDuration()
		 */
		public long getRemainingDuration()
		{
			return getMaxDuration() - getDuration();
		}

		/**
		 * Determines if a transition should occur based upon the current duration and poll count. Specifically, transition should occur if either the duration or
		 * the poll count is above the current maximum, or both are above their minimums.
		 * @return <code>true</code> if this state should transition.
		 */
		public boolean isTransitionReady()
		{
			final long duration = System.currentTimeMillis() - startTime;
			return duration > getMaxDuration() || pollCount > getMaxPollCount() || (duration > getMinDuration() && pollCount > getMinPollCount());
		}

		/** Resets the information of this state by setting the poll count to zero and initializing the time to the current time. */
		public void reset()
		{
			startTime = System.currentTimeMillis();
			pollCount = 0;
		}

		/**
		 * Constructor.
		 * @param state The state.
		 * @param minDuration The minimum duration, in milliseconds, before a transition.
		 * @param maxDuration The maximum duration, in milliseconds, for a transition, regardless of polls.
		 * @param minPollCount The minimum number of polls before a transition.
		 * @param maxPollCount The maximum number of polls before a transition, regardless of duration.
		 * @throws NullPointerException if the given state is <code>null</code>.
		 */
		public StateInfo(final S state, final long minDuration, final long maxDuration, final long minPollCount, final long maxPollCount)
		{
			this.state = checkNotNull(state);
			this.minDuration = minDuration;
			this.maxDuration = maxDuration;
			this.minPollCount = minPollCount;
			this.maxPollCount = maxPollCount;
			reset(); //start with reset info
		}

	}

}
