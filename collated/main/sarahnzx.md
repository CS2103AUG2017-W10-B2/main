# sarahnzx
###### \java\seedu\address\commons\events\ui\JumpToListRequestEvent.java
``` java
    public JumpToListRequestEvent(Index targetIndex, String socialType) {
        this.targetIndex = targetIndex.getZeroBased();
        this.socialType = socialType;
    }
```
###### \java\seedu\address\commons\events\ui\JumpToListRequestEvent.java
``` java
    public String getSocialType() {
        return this.socialType;
    }
```
###### \java\seedu\address\commons\events\ui\PersonPanelSelectionChangedEvent.java
``` java
    public PersonPanelSelectionChangedEvent(PersonCard newSelection, String socialType) {
        this.newSelection = newSelection;
        this.socialType = socialType;
    }
```
###### \java\seedu\address\commons\events\ui\PersonPanelSelectionChangedEvent.java
``` java
    public String getSocialType() {
        return socialType;
    }
}
```
###### \java\seedu\address\logic\commands\DeleteCommand.java
``` java
    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person(s): %1$s";

    private List<Index> targetIndexList = new ArrayList<>();

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        Collection<ReadOnlyPerson> personsToDelete = getPersonsToDelete();
        StringBuilder deletedPersons = new StringBuilder();

        for (ReadOnlyPerson personToDelete : personsToDelete) {
            try {
                model.deletePerson(personToDelete);
            } catch (PersonNotFoundException pnfe) {
                assert false : "The target person cannot be missing";
            }

            deletedPersons.append("\n");
            deletedPersons.append(personToDelete);
        }
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, deletedPersons));
    }

```
###### \java\seedu\address\logic\commands\SelectCommand.java
``` java
    public SelectCommand(Index targetIndex, String socialType) {
        this.targetIndex = targetIndex;
        this.socialType = socialType;
    }
```
###### \java\seedu\address\logic\commands\SelectCommand.java
``` java
    /**
     * Checks if a person has a specified social type.
     */
    private boolean checkPersonHasSocialType(ReadOnlyPerson selectedPerson, String socialType) {
        Set<SocialInfo> selectedPersonSocialInfos = selectedPerson.getSocialInfos();
        boolean hasSameSocialType = false;
        // iterate through the SocialInfos of the selected person
        for (SocialInfo si : selectedPersonSocialInfos) {
            if (si.getSocialType().equals(socialType)) {
                hasSameSocialType = true;
                break;
            }
        }
        return hasSameSocialType;
    }
```
###### \java\seedu\address\logic\parser\ArgumentMultimap.java
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
###### \java\seedu\address\logic\parser\ParserUtil.java
``` java
    /**
     * Checks if the specified social type is valid.
     */
    public static Optional<String> parseSelect(String arg) throws IllegalValueException {
        requireNonNull(arg);
        if (!(arg.equals(FACEBOOK_IDENTIFIER) || arg.equals(INSTAGRAM_IDENTIFIER)
                || arg.equals(FACEBOOK_IDENTIFIER_ALIAS) || arg.equals(INSTAGRAM_IDENTIFIER_ALIAS))) {
            throw new IllegalValueException(MESSAGE_INVALID_SOCIAL_TYPE);
        }
        return Optional.of(getSocialType(arg));
    }
```
###### \java\seedu\address\logic\parser\SelectCommandParser.java
``` java
    /**
     * Parses the given {@code String} of arguments in the context of the SelectCommand
     * and returns an SelectCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SelectCommand parse(String args) throws ParseException {
        try {
            String trimmedArgs = args.trim();
            String[] tokens = trimmedArgs.split(" ");
            String indexStr = tokens[0];
            String socialType = null;
            if (tokens.length > 1) {
                // if there is more than one argument
                socialType = ParserUtil.parseSelect(tokens[1]).get();
            }
            Index index = ParserUtil.parseIndex(indexStr);
            return new SelectCommand(index, socialType);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\logic\parser\SocialInfoMapping.java
``` java
    public static String getSocialType(String socialType) throws IllegalValueException {
        if (socialType.equals(FACEBOOK_IDENTIFIER) || socialType.equals(FACEBOOK_IDENTIFIER_ALIAS)) {
            return FACEBOOK_IDENTIFIER;
        } else if (socialType.equals(INSTAGRAM_IDENTIFIER) || socialType.equals(INSTAGRAM_IDENTIFIER_ALIAS)) {
            return INSTAGRAM_IDENTIFIER;
        } else {
            throw new IllegalValueException(UNRECOGNIZED_SOCIAL_TYPE_MESSAGE);
        }
    }

```
###### \java\seedu\address\model\ModelManager.java
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
###### \java\seedu\address\model\person\Phone.java
``` java
    public final List<String> phonelist;

```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        ReadOnlyPerson person = event.getNewSelection().person;
        String requestedSocialType = event.getSocialType();
        Person p = new Person(person);
        Iterator<SocialInfo> iterator = p.getSocialInfos().iterator();
        if (iterator.hasNext()) {
            // if there is SocialInfo stored
            SocialInfo social = iterator.next();
            String socialType = social.getSocialType();
            while (!socialType.equals(requestedSocialType) && iterator.hasNext() && requestedSocialType != null) {
                // if no social type is specified, the default social type shown will be Instagram
                social = iterator.next();
            }

            String url = social.getSocialUrl();
            loadPage(url);
        } else {
            loadPersonPage(event.getNewSelection().person);
        }
    }

```
###### \java\seedu\address\ui\PersonListPanel.java
``` java
    @Subscribe
    private void handleJumpToListRequestEvent(JumpToListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        this.socialType = event.getSocialType();
        scrollTo(event.targetIndex);
        PersonCard currentSelected = personListView.getSelectionModel().getSelectedItem();
        raise(new PersonPanelSelectionChangedEvent(currentSelected, socialType));
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<PersonCard> {

        @Override
        protected void updateItem(PersonCard person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(person.getRoot());
            }
        }
    }

}
```
