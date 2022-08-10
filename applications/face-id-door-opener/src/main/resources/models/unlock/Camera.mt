package unlock;

import FaceUnlock.*;
component Camera {
    port out Person.Image image;

    int click = 0;
    every 1s {
      log("[Camera-Montithings] took picture");
      image = :Person.Image{
        person_id = click % 5;
      };
      click++;
    }
}
