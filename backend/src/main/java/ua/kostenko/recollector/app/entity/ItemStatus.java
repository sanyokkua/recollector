package ua.kostenko.recollector.app.entity;

/**
 * Enumeration representing the possible statuses of an item.
 * <p>
 * The statuses indicate the current state of the item and are used to track progress:
 * <ul>
 * <li>FINISHED - The item has been completed.</li>
 * <li>IN_PROGRESS - The item is currently being worked on.</li>
 * <li>TODO_LATER - The item is planned for future action.</li>
 * </ul>
 * </p>
 */
public enum ItemStatus {
    FINISHED,
    IN_PROGRESS,
    TODO_LATER
}
