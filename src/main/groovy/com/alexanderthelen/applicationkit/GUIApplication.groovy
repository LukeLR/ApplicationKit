package com.alexanderthelen.applicationkit

import com.alexanderthelen.guikit.WindowController
import javafx.collections.FXCollections
import javafx.stage.Stage

abstract class GUIApplication extends javafx.application.Application implements Application {
    static GUIApplication instance

    final List<WindowController> windowControllers = FXCollections.observableArrayList()

    GUIApplication() {
        instance = this
    }

    @Override
    void start(Stage primaryStage) throws Exception {
        run()
    }

    void show(Class transition) {
        windowControllers.each { WindowController windowController ->
            windowController.show(transition)
        }
    }

    void show() {
        show(null)
    }

    void hide(Class transition) {
        windowControllers.each { WindowController windowController ->
            windowController.hide(transition)
        }
    }

    void hide() {
        hide(null)
    }

    abstract void run()

    static GUIApplication getInstance() {
        return instance
    }
}
