package com.alexanderthelen.applicationkit

import com.alexanderthelen.systemkit.Describable
import javafx.scene.image.Image

trait Application implements Describable {
    String title
    Image image
}
