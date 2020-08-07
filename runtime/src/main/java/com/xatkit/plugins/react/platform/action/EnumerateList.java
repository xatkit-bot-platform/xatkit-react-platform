package com.xatkit.plugins.react.platform.action;

import com.xatkit.execution.StateContext;
import com.xatkit.plugins.chat.platform.action.FormatList;
import com.xatkit.plugins.react.platform.ReactPlatform;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Formats the provided {@link List} into an enumeration that can be embedded in React messages.
 * <p>
 * The provided {@link List} is stored in the {@link StateContext} with the {@link FormatList#LAST_FORMATTED_LIST}
 * key, allowing to retrieve and manipulate it in custom actions.
 * <p>
 * This action supports any kind of {@link List}, and calls the {@link Object#toString()} method on each object.
 */
public class EnumerateList extends FormatList<ReactPlatform> {

    /**
     * Constructs an {@link EnumerateList} with the provided {@code runtimePlatform}, {@code session}, and {@code list}.
     *
     * @param platform the {@link ReactPlatform} containing this action
     * @param context  the {@link StateContext} associated to this action
     * @param list     the {@link List} to format as a set of items
     */
    public EnumerateList(@NonNull ReactPlatform platform, @NonNull StateContext context, @NonNull List<?> list) {
        super(platform, context, list, null);
    }

    public EnumerateList(@NonNull ReactPlatform platform, @NonNull StateContext context, @NonNull List<?> list,
                         @Nullable String formatterName) {
        super(platform, context, list, formatterName);
    }

    /**
     * Formats the provided {@link List} into an enumeration.
     * <p>
     * Each item is represented with the following pattern: {@code "[index] item.toString()\n"}.
     *
     * @return the formatted {@link String}
     */
    @Override
    protected Object formatList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append("[")
                    .append(i)
                    .append("] ")
                    .append(formatter.format(list.get(i)))
                    .append("  \n");
        }
        return sb.toString();
    }
}
