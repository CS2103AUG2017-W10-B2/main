# keithsoc-reused
###### /main/java/seedu/address/ui/UiResize.java
``` java
/**
 * Manages resizing of UI Window by adding mouse event listeners to each UI component.
 */
public class UiResize {

    /**
     * Registers an event handler for the Scene in the specified Stage (top-level UI container)
     * @param stage
     */
    public static void enableResizableWindow(Stage stage) {
        ResizeHandler resizeHandler = new ResizeHandler(stage);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeHandler);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeHandler);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeHandler);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeHandler);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeHandler);
        ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
        for (Node child : children) {
            addHandlerToNodes(child, resizeHandler);
        }
    }

    /**
     * Registers an event handler for each UI node in the hierarchy
     * @param node
     * @param handler
     */
    public static void addHandlerToNodes(Node node, EventHandler<MouseEvent> handler) {
        node.addEventHandler(MouseEvent.MOUSE_MOVED, handler);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, handler);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, handler);
        node.addEventHandler(MouseEvent.MOUSE_EXITED, handler);
        node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, handler);
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            ObservableList<Node> children = parent.getChildrenUnmodifiable();
            for (Node child : children) {
                addHandlerToNodes(child, handler);
            }
        }
    }

    /**
     * Configures mouse event handler
     */
    static class ResizeHandler implements EventHandler<MouseEvent> {
        private Stage stage;
        private Cursor cursorEvent = Cursor.DEFAULT;
        private int border = 4;
        private double startX = 0;
        private double startY = 0;

        public ResizeHandler(Stage stage) {
            this.stage = stage;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            Scene scene = stage.getScene();

            double mouseEventX = mouseEvent.getSceneX();
            double mouseEventY = mouseEvent.getSceneY();
            double sceneWidth = scene.getWidth();
            double sceneHeight = scene.getHeight();

            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType) == true) {
                if (mouseEventX < border && mouseEventY < border) {
                    cursorEvent = Cursor.NW_RESIZE;
                } else if (mouseEventX < border && mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.SW_RESIZE;
                } else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
                    cursorEvent = Cursor.NE_RESIZE;
                } else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.SE_RESIZE;
                } else if (mouseEventX < border) {
                    cursorEvent = Cursor.W_RESIZE;
                } else if (mouseEventX > sceneWidth - border) {
                    cursorEvent = Cursor.E_RESIZE;
                } else if (mouseEventY < border) {
                    cursorEvent = Cursor.N_RESIZE;
                } else if (mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.S_RESIZE;
                } else {
                    cursorEvent = Cursor.DEFAULT;
                }
                scene.setCursor(cursorEvent);
            } else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType)
                    || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
                scene.setCursor(Cursor.DEFAULT);
            } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType) == true) {
                startX = stage.getWidth() - mouseEventX;
                startY = stage.getHeight() - mouseEventY;
            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType) == true) {

                if (Cursor.DEFAULT.equals(cursorEvent) == false) {
                    if (Cursor.W_RESIZE.equals(cursorEvent) == false && Cursor.E_RESIZE.equals(cursorEvent) == false) {
                        double minHeight = stage.getMinHeight() > (border * 2) ? stage.getMinHeight() : (border * 2);
                        if (Cursor.NW_RESIZE.equals(cursorEvent) == true
                                || Cursor.N_RESIZE.equals(cursorEvent) == true
                                || Cursor.NE_RESIZE.equals(cursorEvent) == true) {
                            if (stage.getHeight() > minHeight || mouseEventY < 0) {
                                stage.setHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight());
                                stage.setY(mouseEvent.getScreenY());
                            }
                        } else {
                            if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
                                stage.setHeight(mouseEventY + startY);
                            }
                        }
                    }

                    if (Cursor.N_RESIZE.equals(cursorEvent) == false && Cursor.S_RESIZE.equals(cursorEvent) == false) {
                        double minWidth = stage.getMinWidth() > (border * 2) ? stage.getMinWidth() : (border * 2);
                        if (Cursor.NW_RESIZE.equals(cursorEvent) == true
                                || Cursor.W_RESIZE.equals(cursorEvent) == true
                                || Cursor.SW_RESIZE.equals(cursorEvent) == true) {
                            if (stage.getWidth() > minWidth || mouseEventX < 0) {
                                stage.setWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth());
                                stage.setX(mouseEvent.getScreenX());
                            }
                        } else {
                            if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
                                stage.setWidth(mouseEventX + startX);
                            }
                        }
                    }
                }
            }
        }
    }
}
```
