import themidibus.*; //Import the library
import controlP5.*;
import com.cage.colorharmony.*;


ControlFrame cf;
color colorValue;
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
color[] colors = new color[8];

void settings(){
  size(640, 400, P3D);
  //size(1280, 800, P3D);
}

void setup(){
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

void draw(){
  background(0);
  //fill(0, 25);
  noStroke();
  rect(0, 0, width, height);

  sketch1.run();


}

void resetColors(){
  colors = colorHarmony.Triads(colorValue);
}

void keyPressed(){
  resetColors();

  sketch1.keyAddBand();
}















// Midi management

void noteOn(int channel, int pitch, int velocity) {
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

void noteOff(int channel, int pitch, int velocity) {
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

void controllerChange(int channel, int number, int value) {
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