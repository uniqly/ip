package ekud.task;

import ekud.Ekud;
import ekud.components.Ui;
import ekud.exceptions.EkudException;

import java.util.HashMap;

public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) throws EkudException {
        if (description == null || description.isEmpty()) {
            throw new EkudException(getEmptyDescriptionErrorMessage());
        }
        this.description = description;
        isDone = false;
    }

    public static Task getTaskFromSave(String taskSaveString, Ui ui) {
        try {
            taskSaveString = taskSaveString.trim(); // removes extra new lines
            String[] args = taskSaveString.split("(\\s\\|\\s)"); // splits string along < | > delimiter

            String type = args[0];
            boolean isDone = args[1].equals("1");
            Task task = switch (type) {
                case "T" -> new TodoTask(args[2]);
                case "D" -> new DeadlineTask(args[2], args[3]);
                case "E" -> new EventTask(args[2], args[3], args[4]);
                default -> null;
            };

            if (task != null) {
                task.isDone = isDone;
            }

            return task;

        } catch (IndexOutOfBoundsException | EkudException e) {
            String message = String.format(
                        "Warning: ekud.task.Task entry { %s } is missing required arguments or is incorrectly formatted"
                                + "\nRemoving ekud.task entry...",
                        taskSaveString);
            ui.printOutput(message);
            return null;
        }
    }

    public static Task getTaskFromTokens(HashMap<String, String> tokens) throws EkudException {
        String type = tokens.get("command").toLowerCase();
        String argument = tokens.get("argument");

        return switch (type) {
            case "todo" -> new TodoTask(argument);
            case "deadline" -> new DeadlineTask(argument, tokens.get("/by"));
            case "event" -> new EventTask(argument, tokens.get("/from"), tokens.get("/to"));
            default -> throw new EkudException("Wow! What is this type of ekud.task?\nI'm not sure how to process this");
        };
    }

    public abstract String getEmptyDescriptionErrorMessage();

    public String getSaveTaskString() {
        int statusInt = (isDone ? 1 : 0);
        return String.format("%d | %s", statusInt, description);
    }

    public boolean isDone() {
        return isDone;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns {@code true} if the description contains the keyword.
     *
     * @param keyword The {@link String} to look for.
     * @return If the description contains the keyword.
     */
    public boolean hasMatchingDescription(String keyword) {
        return (description.contains(keyword));
    }

    @Override
    public String toString() {
        // formats ekud.task as "[statusIcon] description"
        return String.format("[%s] %s", getStatusIcon(), description);
    }
}
