class ControlFrame extends PApplet {

  int w, h;
  PApplet parent;
  ControlP5 cp5;

  public RadioButton r1, r2;

  public ControlFrame(PApplet _parent, int _w, int _h, String _name) {
    super();
    parent = _parent;
    w=_w;
    h=_h;
    PApplet.runSketch(new String[]{this.getClass().getName()}, this);
  }

  public void settings() {
    size(w, h);
  }

  public void setup() {
    surface.setLocation(10, 10);
    cp5 = new ControlP5(this);

    cp5.addColorWheel("colorValue")
        .setPosition(10, 10)
        .plugTo(parent, "colorValue")
        .setRGB(color(128, 0, 255));

    // cp5.addToggle("auto")
    //    .plugTo(parent, "auto")
    //    .setPosition(10, 70)
    //    .setSize(50, 50)
    //    .setValue(true);
    //
    // cp5.addKnob("blend")
    //    .plugTo(parent, "c3")
    //    .setPosition(100, 300)
    //    .setSize(200, 200)
    //    .setRange(0, 255)
    //    .setValue(200);
    //
    // cp5.addNumberbox("color-red")
    //    .plugTo(parent, "c0")
    //    .setRange(0, 255)
    //    .setValue(255)
    //    .setPosition(100, 10)
    //    .setSize(100, 20);
    //
    // cp5.addNumberbox("color-green")
    //    .plugTo(parent, "c1")
    //    .setRange(0, 255)
    //    .setValue(128)
    //    .setPosition(100, 70)
    //    .setSize(100, 20);
    //

    cp5.addSlider("size")
       .plugTo(parent, "cfsize")
       .setRange(0, 200)
       .setValue(10.0)
       .setPosition(10, 240)
       .setSize(200, 30);

     cp5.addSlider("range")
        .plugTo(parent, "cfrange")
        .setRange(0, 200)
        .setValue(10.0)
        .setPosition(10, 280)
        .setSize(200, 30);

      cp5.addSlider("wave speed")
         .plugTo(parent, "numbox1")
         .setRange(0.02, 5.00)
         .setValue(0.002)
         .setPosition(10, 320)
         .setSize(200, 30);

       r1 = cp5.addRadioButton("radioButton")
         .plugTo(parent, "radioButton1")
         .setPosition(10,360)
         .setSize(40,30)
         .setValue(1.0)
         .setItemsPerRow(5)
         .setSpacingColumn(50)
         .addItem("fill",1)
         .addItem("stroke",2)
         .addItem("stroke/fill",3)
         ;

       r2 = cp5.addRadioButton("shapeType")
         .setPosition(10,400)
         .setSize(40,30)
         .setValue(1.0)
         .setItemsPerRow(5)
         .setSpacingColumn(50)
         .addItem("LINES",1)
         .addItem("TRIANGLE_FAN",2)
         .addItem("TRIANGLE_STRIP",3)
         ;

  }

  void draw() {
    background(190);

    radioButton1 = (int)r1.getValue();
  }
}