// (c) https://github.com/MontiCore/monticore
package unlock;

import FaceUnlock.*;
component Camera {
    port out Image.Image image;

    int click = 0;
    every 1s {
      log("[Camera-Montithings] took picture");
      image = :Image.Image{
        person_id = click % 5;
      };
      click++;
    }
}
