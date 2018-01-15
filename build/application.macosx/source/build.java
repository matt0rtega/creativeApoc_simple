import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import themidibus.*; 
import controlP5.*; 
import com.cage.colorharmony.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class build extends PApplet {

 //Import the library




ControlFrame cf;
int colorValue;
float cfsize;
float cfrange;
float numbox1;
int radioButton1;

// Midi Manager

MidiBus myBus; // The MidiBus

boolean noteOn = false;
int pitch = 0;
int size = 30;

int reverb = 0;
int delay = 1;
int tempo = 120;

// Sketch Manager
Sketch1 sketch1;


ColorHarmony colorHarmony = new ColorHarmony(this);
int[] colors = new int[8];

public void settings(){
  size(640, 400, P3D);
  //size(1280, 800, P3D);
}

public void setup(){
  background(0);

  sketch1 = new Sketch1();

  cf = new ControlFrame(this, 400, 800, "Controls");
  surface.setLocation(420, 10);
  noStroke();

  MidiBus.list();
  myBus = new MidiBus(this, 1, -1);

  // GET A MONOCHROMATIC PALETTE, BASED ON A RANDOM BASE COLOR
  colors = colorHarmony.Monochromatic(colorValue);
}

public void draw(){
  background(0);
  //fill(0, 25);
  noStroke();
  rect(0, 0, width, height);

  sketch1.run();


}

public void resetColors(){
  colors = colorHarmony.Triads(colorValue);
}

public void keyPressed(){
  resetColors();

  sketch1.keyAddBand();
}















// Midi management

public void noteOn(int channel, int pitch, int velocity) {
  // Receive a noteOn
  //println();
  //println("Note On:");
  //println("--------");
  //println("Channel:"+channel);
  //println("Pitch:"+pitch);
  //println("Velocity:"+velocity);

  // Assignments

  noteOn = true;
  this.pitch = pitch;

  resetColors();
  sketch1.addBand();
}

public void noteOff(int channel, int pitch, int velocity) {
  // Receive a noteOff
  //println();
  //println("Note Off:");
  //println("--------");
  //println("Channel:"+c  hannel);
  //println("Pitch:"+pitch);
  //println("Velocity:"+velocity);

  // Assignments
  noteOn = false;
}

public void controllerChange(int channel, int number, int value) {
  // Receive a controllerChange
  println();
  println("Controller Change:");
  println("--------");
  println("Channel:"+channel);
  println("Number:"+number);
  println("Value:"+value);
  println("Reverb:"+reverb);
  println("Delay:"+delay);

  // Assignments

  if (number == 61) reverb = value;
  if (number == 62) delay = value;
  //if (number = 24) tempo = value;

}
class Band extends Particle {

  PVector bulgeTarget, velocity;
  float bulgeSize;
  float bandwidth;
  float x1 = 0;
  float x2 = 0;
  float range;
  float theta;
  float thetaOffset = random(1000);
  int mode;
  float rangeTarget;
  int polyType;

  int co;

  float strength;

  Band (float x, float y, float bulgeSize, int colorValue, float range, float theta, int mode){
    super(x, y);

    strength = 100;
    this.range = range;
    rangeTarget = range;
    bandwidth = 50;
    bulgeTarget = new PVector(0, random(height));
    this.bulgeSize = bulgeSize;
    this.theta = theta;
    this.mode = mode;
    velocity = new PVector(0, random(-1, 1));
    polyType = (int)cf.r2.getValue();
    if(random(1) < 0.2f) polyType = (int)random(1, 5);

    co = colorValue;

    x1 = -bandwidth;
    x2 = bandwidth;
  }

  public void run(){
    update();
    display();
  }

  public void update(){
    float d = rangeTarget - range;
    range += d * 0.02f;

    lifespan -= 0.2f;
  }

  public void showTarget(){
    pushMatrix();
    translate(location.x, location.y);

    ellipseMode(CENTER);
    ellipse(0, bulgeTarget.y, bulgeSize, bulgeSize);
    noStroke();
    fill(co, lifespan);
    popMatrix();
  }

  public void display(){

    bulgeTarget.add(velocity);

    pushMatrix();
    translate(location.x, location.y);

    switch(mode){
      case 1:
        noStroke();
        fill(co, lifespan);
      break;
      case 2:
        noFill();
        strokeWeight(1);
        stroke(co, lifespan);
      break;
      case 3:
        strokeWeight(1);
        fill(co, lifespan);
        stroke(0, lifespan);
      break;
    }

    if(polyType == 1) {beginShape(LINES); stroke(co, lifespan); strokeWeight(5);}
    if(polyType == 2) beginShape(TRIANGLES);
    if(polyType == 3) beginShape(TRIANGLE_STRIP);
    if(polyType == 4) {beginShape(POINTS); stroke(co, lifespan); strokeWeight(10);}


    for(int y=-height/2; y<=height+height/2; y+=50){

      float offsetx = map(noise(frameCount * 0.002f + (y * theta) + thetaOffset), -1, 1, 0, range);

      PVector dir = PVector.sub(bulgeTarget, new PVector(x1, y));
      float d = dir.mag();
      d = constrain(d, 5, 100);
      dir.normalize();
      float push = -1 * strength / (d * d);
      dir.mult(push);
      dir.setMag(bulgeSize);

      PVector dir2 = PVector.sub(bulgeTarget, new PVector(x2, y));
      float d2 = dir2.mag();
      d2 = constrain(d2, 5, 100);
      dir2.normalize();
      float push2 = -1 * strength / (d2 * d2);
      dir2.mult(push2);
      dir2.setMag(bulgeSize);
      vertex(x1 + dir.x - offsetx, y + dir.y);
      vertex(x2 + dir2.x + offsetx, y + dir2.y);
    }
    endShape();

    popMatrix();
  }

}
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
       .setValue(10.0f)
       .setPosition(10, 240)
       .setSize(200, 30);

     cp5.addSlider("range")
        .plugTo(parent, "cfrange")
        .setRange(0, 200)
        .setValue(10.0f)
        .setPosition(10, 280)
        .setSize(200, 30);

      cp5.addSlider("wave speed")
         .plugTo(parent, "numbox1")
         .setRange(0.02f, 5.00f)
         .setValue(0.002f)
         .setPosition(10, 320)
         .setSize(200, 30);

       r1 = cp5.addRadioButton("radioButton")
         .plugTo(parent, "radioButton1")
         .setPosition(10,360)
         .setSize(40,30)
         .setValue(1.0f)
         .setItemsPerRow(5)
         .setSpacingColumn(50)
         .addItem("fill",1)
         .addItem("stroke",2)
         .addItem("stroke/fill",3)
         ;

       r2 = cp5.addRadioButton("shapeType")
         .setPosition(10,400)
         .setSize(40,30)
         .setValue(1.0f)
         .setItemsPerRow(5)
         .setSpacingColumn(50)
         .addItem("LINES",1)
         .addItem("TRIANGLE_FAN",2)
         .addItem("TRIANGLE_STRIP",3)
         ;

  }

  public void draw() {
    background(190);

    radioButton1 = (int)r1.getValue();
  }
}
class Particle {
  PVector location;
  PVector velocity;
  PVector acceleration;
  float lifespan;

  Particle(float x, float y) {
    location = new PVector(x, y);
    velocity = new PVector(5, 0);
    acceleration = new PVector(0, 0);
    lifespan = 255.0f;
  }

  public void run() {
    update();
    display();
  }

  // Method to update position
  public void update() {
    //location.add(velocity);
    //velocity.add(acceleration);
    lifespan -= 1.0f;
  }

  public void display() {
    stroke(0, lifespan);
    fill(175, 200, lifespan);
    ellipse(location.x, location.y, 8, 8);
  }

  // Is the particle still useful?
  public boolean isDead() {
    if (lifespan<0) {
      return true;
    } else {
      return false;
    }
  }
}
class Sketch1 {

  ArrayList<Band> bands;

  Sketch1(){

    bands = new ArrayList<Band>();
  }

  public void run(){
    update();
  }

  public void update(){
    for (int i = bands.size()-1; i >= 0; i--) {
      Band b = bands.get(i);
      b.run();
      if (b.isDead()) {
        bands.remove(i);
      }
    }

    if(bands.size() > 1){
      Band b = bands.get(bands.size()-1);

      if(noteOn){
        b.lifespan = 150;
        b.rangeTarget += 2;
      }
    }

    //println(bands.size());
  }

  public void addBand(){
    int c = colors[(int)random(8)];

    pitch = (int)map(pitch, 37, 90, 0, width);

    bands.add(new Band(pitch, 0, cfsize, c, cfrange, numbox1 / 10, radioButton1));
  }
  
  public void keyAddBand(){
    int c = colors[(int)random(8)];

    pitch = (int)map(pitch, 37, 90, 0, width);

    bands.add(new Band(random(width), 0, cfsize, c, cfrange, numbox1 / 10, radioButton1));
  }


}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "build" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
