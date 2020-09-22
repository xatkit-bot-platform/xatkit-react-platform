package com.xatkit.plugins.react.platform.action;

import com.xatkit.core.platform.action.RuntimeAction;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.react.platform.ReactPlatform;
import fr.inria.atlanmod.commons.log.Log;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;

/**
 * Waits a given delay.
 */
public class Wait extends RuntimeAction<ReactPlatform> {

    /**
     * The delay to wait.
     */
    private int delay;

    /**
     * Constructs a new {@link Wait} with the provided {@code reactPlatform}, {@code session}, and {@code delay}.
     *
     * @param platform the {@link ReactPlatform} containing this action
     * @param context       the {@link StateContext} associated to this action
     * @param delay         the delay to wait
     * @throws IllegalArgumentException if the provided {@code delay} is lower or equal to {@code 0}
     */
    public Wait(ReactPlatform platform, StateContext context, int delay) {
        super(platform, context);
        checkArgument(delay > 0, "Cannot construct a %s action with the provided delay %s, expecting a value greater " +
                "or equal to 0", this.getClass().getSimpleName(), delay);
        this.delay = delay;
    }

    /**
     * Waits the given delay.
     *
     * @return {@code null}
     */
    @Override
    protected Object compute() {
        try {
            Thread.sleep(this.delay);
        } catch (InterruptedException e) {
            Log.error(e, "An error occurred when computing {0}, see attached exception",
                    this.getClass().getSimpleName());
        }
        return null;
    }
}
