import themidibus.*;
import com.cage.colorharmony.*;
import java.util.Iterator;

int size = 30;

// Midi Manager

// From Leon
MidiBus myBus, touchOSC; // The MidiBus

boolean noteOn = false;
int pitch = 0;
int reverb = 0;
int delay = 1;
int tempo = 120;

// From Launch Controller
int toggle1 = 1;
int toggle2 = 0;
int toggle3 = 0;
int toggle4 = 0;

float r,g,b = 0;
int knob4 = 3;
float knob5 = 50;
float knob6 = 50;
float knob7 = 50;
int knob8 = 1;
float knob9 = 0.02;
float knob10 = 300;
float knob11 = 50;
float knob12 = 25;
float knob13 = 25;



// Sketch Manager
Sketch1 sketch1;

float fade = 25;
int numPlanes = 1;

ColorHarmony colorHarmony = new ColorHarmony(this);
color[] colors = new color[24];

PImage img;
PGraphics canvas;

float time;

void settings(){
  
  // Sizes for projection
  //size(1024, 800, P3D);
  //size(1280, 800, P3D);
  fullScreen(P3D);

}

void setup(){
  background(0);
  //frameRate(30);
  sketch1 = new Sketch1();
  
  img = createImage(width, height, RGB);
  canvas = createGraphics(width, height, P3D);
  
  time = 0;

  MidiBus.list();

  // Midi setup for performance and launch control
  touchOSC = new MidiBus(this, "TouchOSC Bridge", -1);
  myBus = new MidiBus(this, "USB Uno MIDI Interface", -1);

  // GET A MONOCHROMATIC PALETTE, BASED ON A RANDOM BASE COLOR
  colors = colorHarmony.Monochromatic(color(r, g, b));
  
}

void draw(){
  
  time = millis()*0.016;

  //translate(0, mouseY);

  fade = knob12;
  //fade = map(reverb, 0, 127, 25, 0);
  rectMode(CORNER);
  fill(0, fade);
  noStroke();
  rect(0, 0, width, height);

  numPlanes = (int)map(delay, 0, 127, 1, 50);


  sceneManager();
  
}

void sceneManager(){

  sketch1.run();
  sketch1.createCircles();
  
  if(toggle1 == 1 && toggle3 == 1 && random(1) < 0.02) { resetColors(); sketch1.keyAddBand();}
}

void resetColors(){
  colors = colorHarmony.Analogous(color(r, g, b));
}

void mousePressed(){
  resetColors();
  if(toggle1 == 1) sketch1.keyAddBand();

  println("Mousepressed");
}















// Midi management

void noteOn(int channel, int pitch, int velocity) {
  // Receive a noteOn
  println();
  println("Note On:");
  println("--------");
  println("Channel:"+channel);
  println("Pitch:"+pitch);
  println("Velocity:"+velocity);

  // Assignments

  noteOn = true;
  this.pitch = pitch;

  resetColors();
  if(toggle1 == 1) sketch1.addBand();
}

void noteOff(int channel, int pitch, int velocity) {
  // Receive a noteOff
  println();
  println("Note Off:");
  println("--------");
  println("Channel:"+channel);
  println("Pitch:"+pitch);
  println("Velocity:"+velocity);

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

  if (number == 9) toggle1 = value;
  if (number == 10) toggle2 = value;
  if(number == 14) toggle3 = value;
  if(number == 13) toggle4 = value;

  // Color control
  if (number == 21) r = map(value, 0, 127, 0, 255);
  if (number == 22) g = map(value, 0, 127, 0, 255);
  if (number == 23) b = map(value, 0, 127, 0, 255);

  if (number == 24) knob4 = (int)map(value, 0, 4, 1, 4); // type selector
  if (number == 25) knob5 = map(value, 0, 127, 1, 100); // band width
  if (number == 26) knob6 = map(value, 0, 127, 1, 200); // noise
  if (number == 27) knob7 = map(value, 0, 127, 1, 200);
  if (number == 28) knob8 = (int)map(value, 0, 127, 1, 3); //println(knob8); 
  if (number == 29) knob9 = map(value, 0, 127, 0.002, 0.05); // band noise speed
  if (number == 30) knob10 = map(value, 0, 127, 10, width); // distance trigger
  if (number == 32) knob12 = map(value, 0, 127, 0, 25); // fade
  
}