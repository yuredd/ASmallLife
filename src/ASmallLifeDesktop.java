package com.yuredd.asmalllife;
import com.badlogic.gdx.backends.jogl.JoglApplication;

public class ASmallLifeDesktop {
  public static void main(String[] argv) {
    new JoglApplication(new ASmallLife(), "A Small Life by yuredd", 800, 600, false);
  }
}

