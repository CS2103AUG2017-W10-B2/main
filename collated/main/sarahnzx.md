# sarahnzx
###### /java/seedu/address/logic/commands/DeleteCommand.java
``` java
    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person(s): %1$s";

    private List<Index> targetIndexList = new ArrayList<>();

    public DeleteCommand(List<Index> targetIndexList) {
        this.targetIndexList = targetIndexList;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {

        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        StringBuilder people = new StringBuilder();

        for (Index i : this.targetIndexList) {
            if (i.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            ReadOnlyPerson personToDelete = lastShownList.get(i.getZeroBased());

            try {
                model.deletePerson(personToDelete);
            } catch (PersonNotFoundException pnfe) {
                assert false : "The target person cannot be missing";
            }

            people.append("\n");
            people.append(personToDelete);
        }
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, people));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && this.targetIndexList.equals(((DeleteCommand) other).targetIndexList)); // state check
    }
}
```
###### /java/seedu/address/logic/parser/ArgumentMultimap.java
``` java
    /**
     * Returns multiple values of {@code prefix}.
     */
    public Optional<String> getMultipleValues(Prefix prefix) {
        List<String> values = getAllValues(prefix);
        ArrayList<String> added = new ArrayList<>();
        String str = "";
        for (String v : values) {
            if (!added.contains(v)) {
                str += v + "\n";
                added.add(v);
            }
        }
        if (!str.isEmpty()) {
            str = str.substring(0, str.length() - 1);
        }
        return str.isEmpty() ? Optional.empty() : Optional.of(str);
    }
```
###### /java/seedu/address/model/ModelManager.java
``` java
    @Override
    public void removeTag(Tag tag) throws PersonNotFoundException, DuplicatePersonException {
        for (int i = 0; i < addressBook.getPersonList().size(); i++) {
            ReadOnlyPerson oldPerson = addressBook.getPersonList().get(i);
            Person newPerson = new Person(oldPerson);
            Set<Tag> newTags = newPerson.getTags();
            newTags.remove(tag);
            newPerson.setTags(newTags);
            addressBook.updatePerson(oldPerson, newPerson);
        }
    }
```
###### /java/seedu/address/model/person/Phone.java
``` java
    public final List<String> phonelist;

    /**
     * Validates given phone number.
     *
     * @throws IllegalValueException if given phone string is invalid.
     */
    public Phone(String phone) throws IllegalValueException {
        requireNonNull(phone);
        String[] numbers = phone.split("\n");

        List<String> phones = new ArrayList<>();
        boolean invalid = false;
        String phoneStr = "";

        for (int i = 0; i < numbers.length; i++) {
            String trimmedPhone = numbers[i].trim();
            if (isValidPhone(trimmedPhone)) {
                phones.add(numbers[i]);
                phoneStr += numbers[i] + "\n";
            } else {
                invalid = true;
            }
        }

        if (phones.isEmpty() && invalid) {
            throw new IllegalValueException(MESSAGE_PHONE_CONSTRAINTS);
        }

        if (!phoneStr.isEmpty()) {
            phoneStr = phoneStr.substring(0, phoneStr.length() - 1);
        }

        this.phonelist = phones;
        this.value = phoneStr;
    }
```
