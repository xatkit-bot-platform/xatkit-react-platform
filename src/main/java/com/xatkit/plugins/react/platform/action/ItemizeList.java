package com.xatkit.plugins.react.platform.action;

import com.xatkit.execution.StateContext;
import com.xatkit.plugins.chat.platform.action.FormatList;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.utils.MessageUtils;
import lombok.NonNull;

import java.util.List;

/**
 * Formats the provided {@link List} into a set of items that can be embedded in React messages.
 * <p>
 * The provided {@link List} is stored in the {@link StateContext} with the {@link FormatList#LAST_FORMATTED_LIST}
 * key, allowing to retrieve and manipulate it in custom actions.
 * <p>
 * This action supports any kind of {@link List}, and call the {@link Object#toString()} method on each object.
 * <p>
 * <b>Deprecated</b>: use {@link MessageUtils#itemizeList(List)}.
 */
@Deprecated
public class ItemizeList extends FormatList<ReactPlatform> {

    /**
     * Constructs a {@link ItemizeList} with the provided {@code runtimePlatform}, {@code session}, and {@code list}.
     *
     * @param platform the {@link ReactPlatform} containing this action
     * @param context  the {@link StateContext} associated to this action
     * @param list     the {@link List} to format as a set of items
     */
    public ItemizeList(@NonNull ReactPlatform platform, @NonNull StateContext context, @NonNull List<?> list) {
        super(platform, context, list);
    }

    /**
     * Formats the provided {@link List} into a set of items.
     * <p>
     * Each item is represented with the following pattern: {@code "- item.toString()\n"}.
     * <b>Deprecated</b>: see {@link MessageUtils#itemizeList(List)}.
     *
     * @return the formatted {@link String}
     */
    @Deprecated
    @Override
    protected Object formatList() {
        return MessageUtils.itemizeList(this.list);
    }
}
