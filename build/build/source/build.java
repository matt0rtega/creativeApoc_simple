import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import themidibus.*; 
import com.cage.colorharmony.*; 
import java.util.Iterator; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class build extends PApplet {





int size = 30;

// Midi Manager

// From Leon
MidiBus myBus, novation; // The MidiBus

boolean noteOn = false;
int pitch = 0;
int reverb = 0;
int delay = 1;
int tempo = 120;

// From Launch Controller
int toggle1 = 1;
int toggle2 = 0;

float r,g,b = 0;
int knob4 = 3;
float knob5 = 50;
float knob6 = 50;
float knob7 = 50;
int knob8 = 1;
float knob9 = 0.02f;
float knob10 = 300;
float knob11 = 300;

// Sketch Manager
Sketch1 sketch1;
Sketch2 sketch2;

float fade = 25;
int numPlanes = 1;

ColorHarmony colorHarmony = new ColorHarmony(this);
int[] colors = new int[8];

public void settings(){
  //size(640, 400, P3D);

  // Sizes for projection
  //size(1024, 800, P3D);
  size(1280, 800, P3D);
}

public void setup(){
  background(0);

  sketch1 = new Sketch1();
  sketch2 = new Sketch2();

  MidiBus.list();

  // Midi setup for performance and launch control
  //myBus = new MidiBus(this, "USB Uno MIDI Interface", -1);
  novation = new MidiBus(this, "Launch Control", -1);
  myBus = new MidiBus(this, "To Processing", -1);

  // GET A MONOCHROMATIC PALETTE, BASED ON A RANDOM BASE COLOR
  colors = colorHarmony.Monochromatic(color(r, g, b));
}

public void draw(){

  //translate(0, mouseY);

  fade = map(mouseX, 0, width, 0, 50);
  //fade = map(reverb, 0, 127, 25, 0);
  rectMode(CORNER);
  fill(0, fade);
  noStroke();
  rect(0, 0, width, height);

  numPlanes = (int)map(delay, 0, 127, 1, 50);


  sceneManager();
}

public void sceneManager(){
  // if(toggle1 == 1){
  //   sketch1.run();
  //   sketch1.createCircles();
  // }

  sketch1.run();
  sketch1.createCircles();
  sketch2.run();

  // if(toggle2 == 1){
  //   //fill(255);
  //   //ellipse(random(width), random(height), 100, 100);
  //   sketch2.run();
  // }
}

public void resetColors(){
  colors = colorHarmony.Triads(color(r, g, b));
}

public void mousePressed(){
  resetColors();
  if(toggle1 == 1) sketch1.keyAddBand();
  if(toggle2 == 1) sketch2.keyAddBand();

  println("Keypressed");
}















// Midi management

public void noteOn(int channel, int pitch, int velocity) {
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
  if(toggle2 == 1) sketch2.addCircleRunner();
}

public void noteOff(int channel, int pitch, int velocity) {
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

  if (number == 9) toggle1 = value;
  if (number == 10) toggle2 = value;

  // Color control
  if (number == 21) r = map(value, 0, 127, 0, 255);
  if (number == 22) g = map(value, 0, 127, 0, 255);
  if (number == 23) b = map(value, 0, 127, 0, 255);

  if (number == 24) knob4 = (int)map(value, 0, 127, 1, 4);
  if (number == 25) knob5 = map(value, 0, 127, 1, 50);
  if (number == 26) knob6 = map(value, 0, 127, 1, 200);
  if (number == 27) knob7 = map(value, 0, 127, 1, 200);
  if (number == 28) knob8 = (int)map(value, 0, 127, 1, 3); println(knob8);
  if (number == 29) knob9 = map(value, 0, 127, 0.002f, 0.05f);
  if (number == 30) knob10 = map(value, 0, 127, 10, width);

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
  boolean collision = false;

  int co, lineColor, circleColor;

  float strength;

  Band (float x, float y, float bulgeSize, int colorValue, float range, float theta, int mode){
    super(x, y);

    strength = 100;
    this.range = range;
    rangeTarget = range;
    bulgeTarget = new PVector(x, random(height));
    this.bulgeSize = bulgeSize;
    this.theta = theta;
    this.mode = mode;
    velocity = new PVector(0, random(-1, 1));
    polyType = knob4;
    bandwidth = knob5;
    //if(random(1) < 0.02) polyType = (int)random(1, 5);

    co = colorValue;
    lineColor = colors[(int)random(8)];
    circleColor = colors[(int)random(8)];


    lifespan = 255.0f;

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

    lifespan -= 0.5f;
  }

  public void checkDistance(ArrayList<Band> bands){

    // for (Band other : bands) {
    //   if (other != this) {
    //     float d = PVector.dist(other.bulgeTarget, bulgeTarget);
    //     PVector median = PVector.lerp(other.bulgeTarget, bulgeTarget, 0.5);
    //     if (d < 300) {
    //       strokeWeight(1);
    //       stroke(lineColor);
    //       noFill();
    //       triangle(other.bulgeTarget.x, other.bulgeTarget.y, bulgeTarget.x, bulgeTarget.y, median.x, median.y);
    //       noStroke();
    //       fill(circleColor);
    //       ellipse(median.x, median.y, d/5, d/5);
    //       //if(random(1) < 0.0002) sprites.add(new Sprite(median.x, median.y, random(-0.5, 0.5), random(-0.5, 0.5), random(-0.01, 0.01), random(-0.01, 0.01), d/5));
    //     }
    //   }
    // }

  }

  public void showTarget(){
    pushMatrix();
    ellipseMode(CENTER);
    ellipse(0, bulgeTarget.y, bulgeSize, bulgeSize);
    noStroke();
    fill(co, lifespan);
    popMatrix();
  }

  public void display(){

    bulgeTarget.add(velocity);

    pushMatrix();

    if(polyType == 1) {beginShape(LINES);}
    if(polyType == 2) beginShape(TRIANGLES);
    if(polyType == 3) beginShape(TRIANGLE_STRIP);
    if(polyType == 4) { beginShape(POINTS);}

    for(int y=-height/2; y<=height+height/2; y+=20){

      float offsetx = map(noise(frameCount * 0.002f + (y * theta) + thetaOffset), 0, 1, 0, range);

      PVector dir = PVector.sub(bulgeTarget, new PVector(location.x - bandwidth - offsetx, y));
      float d = dir.mag();
      d = constrain(d, 5, 100);
      dir.normalize();
      float push = -1 * strength / (d * d);
      dir.mult(push);
      dir.setMag(bulgeSize);

      PVector dir2 = PVector.sub(bulgeTarget, new PVector(location.x + bandwidth + offsetx, y));
      float d2 = dir2.mag();
      d2 = constrain(d2, 5, 100);
      dir2.normalize();
      float push2 = -1 * strength / (d2 * d2);
      dir2.mult(push2);
      dir2.setMag(bulgeSize);

      colorSetup(y);
      vertex(location.x - bandwidth + dir.x - offsetx, location.y + y + dir.y);
      vertex(location.x + bandwidth + dir2.x + offsetx, location.y + y + dir2.y);
    }
    endShape();

    popMatrix();
  }

  public void colorSetup(int index){
    float red = map(sin(frameCount * 0.002f + (index * theta * 0.01f)), -1, 1, 0, red(co));
    float green = map(cos(frameCount * 0.001f + (index * theta * 0.01f)), -1, 1, 0, green(co));
    float blue = map(sin(frameCount * 0.003f + (index * theta * 0.01f)), -1, 1, 0, blue(co));

    int gradient = color(red, green, blue);

    fill(co, lifespan);

    switch(mode){
      case 1:
        noStroke();
        if(polyType == 1) { strokeWeight(10); stroke(co, lifespan);}
        fill(co, lifespan);
      break;
      case 2:
        noFill();
        strokeWeight(1);
        stroke(co, lifespan);
      break;
      case 3:

        if(polyType == 4) { strokeWeight(10); stroke(co, lifespan);}
        else {
          strokeWeight(1);
          stroke(0, lifespan);
        }
        fill(co, lifespan);

      break;
    }
  }

}
class CircleRunner extends Particle{

  float size = 0;
  float sizeRange;
  float tsize = random(0.02f, 0.05f);

  PVector target;
  PVector targetVelocity;
  float tx, ty, toffset;
  PVector[] locations;
  PVector[] targets;
  int co = color(255);

  float rowFactor;

  CircleRunner(float x, float y, float rowFactor, int co){
    super(x, y);
    //lifespan = 255.0;
    target = new PVector(x, y);
    targetVelocity = new PVector(random(-1, 1), random(-1, 1));
    tx = random(0.004f, 0.02f);
    ty = random(0.004f, 0.02f);
    toffset = random(10000);
    this.co = co;

    this.rowFactor = rowFactor;

    sizeRange = random(0, 50);

    acceleration = new PVector(random(-0.02f, 0.02f), random(-0.02f, 0.02f));
    velocity = new PVector(random(-1, 1), random(-1, 1));


    locations = new PVector[100];
    for(int i=0; i<locations.length-1; i+=1){
      locations[i] = new PVector(x, y);
    }

  }

  public void run(){
    display();
    update();
    checkEdges();
  }

  public void update(){

    float count = millis()*0.0833f;

    targetVelocity.x = map(noise(count * tx + toffset), 0, 1, -2, 2);
    targetVelocity.y = map(noise(count * ty + toffset), 0, 1, -2, 2);

    target.add(targetVelocity);

    if(keyPressed){
      target = new PVector(random(width), random(height));
    }

    velocity.x = map(sin(count * 0.02f), -1, 1, -1 * 30, 1 * 30);
    velocity.y = map(cos(count * 0.02f), -1, 1, -1 * 30, 1 * 30);

    velocity.limit(2);
    //target.add(velocity);
    velocity.add(acceleration);
    locations[0] = PVector.lerp(locations[0], target, 0.02f);

    for(int i=1; i<locations.length-1; i+=1){
      locations[i] = PVector.lerp(locations[i], locations[i-1], 0.2f);
    }

    acceleration.mult(0);
    //location = PVector.lerp(location, target, 0.02);
    lifespan -= 0.2f;
  }

  public void display(){

    float count = millis()*0.833f;

    for(int i=0; i<locations.length-1; i+=1){
      size = map(noise(count * tsize + (i * 0.02f) * cos(count * 0.001f)), 0, 1, 0, sizeRange);

      float offset = 0;
      offset = i * rowFactor * 0.1f;

      fill(co);
      noStroke();
      ellipse( locations[i].x, locations[i].y + offset, size, size);

    }
  }

  public void createPoly(ArrayList<CircleRunner> circleRunners, ArrayList<Shard> shards){

    for (CircleRunner other : circleRunners){
      if(other != this){
        float d = PVector.dist(other.locations[0], locations[0]);
        if (d < 50) {
          if(random(1) < 0.00012f)shards.add(new Shard(locations[0].x, locations[0].y));
        }

      }
    }

  }

  public void checkEdges() {

    if (target.x > width) {
      target.x = width;
      targetVelocity.x *= -1;
    }
    else if (target.x < 0) {
      targetVelocity.x *= -1;
      target.x = 0;
    }

    if (target.y > height) {
      target.y = height;
      targetVelocity.y *= -1;
    }
    else if (target.y < 0) {
      targetVelocity.y *= -1;
      target.y = 0;
    }
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

  Particle(float x, float y, PVector velocity, PVector acceleration, float lifespan) {
    location = new PVector(x, y);
    this.velocity = velocity;
    this.acceleration = acceleration;
    lifespan = 255.0f;
  }

  public void run() {
    update();
    display();
  }

  // Method to update position
  public void update() {
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
class Shard extends Particle{

  float x1, y1, x2, y2, x3, y3, x4, y4;

  int co;

  Shard(float x, float y) {
    super(x, y);

    lifespan = 255.0f;

    velocity = new PVector(0, random(-0.01f, 0.1f));
    acceleration = new PVector(0, random(-0.01f, 0.01f));

    co = color(random(255), random(255), random(255));

    x1 = random(-100, 0);
    y1 = random(-100, 0);
    x2 = random(0, 100);
    y2 = random(-100, 0);
    x3 = random(0, 100);
    y3 = random(0, 100);
    x4 = random(-100, 0);
    y4 = random(0, 100);
  }

  public void display(){
    pushMatrix();
    fill(co);
    translate(location.x, location.y);
    shape1();
    popMatrix();
  }

  public void update(){
    location.add(velocity);
    velocity.add(acceleration);

    lifespan -= 1.0f;
  }

  public void shape1(){
    PVector v1 = new PVector(-100, -100);

    beginShape();
    vertex(x1, y1);
    vertex(x2, y2);
    fill(co, 0);
    vertex(x3, y3);
    fill(co, 0);
    vertex(x4, y4);
    endShape();
  }

}
class Sketch1 {

  ArrayList<Band> bands;
  ArrayList<Sprite> sprites;

  Sketch1(){

    bands = new ArrayList<Band>();
    sprites = new ArrayList<Sprite>();
  }

  public void run(){
    lights();
    directionalLight(255, 0, 255, mouseX, mouseY, mouseY);

    runBands();
    runSprites();
  }

  public void runBands(){
    for (int i = bands.size()-1; i > 0; i--) {
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

  }



  public void createCircles(){



      for (int i = 0; i < bands.size(); i++) {
        for (int j = 0; j < bands.size(); j++) {
          if (i != j) {
            Band b = bands.get(i);
            Band other = bands.get(j);

            if(b != other){
              float d = PVector.dist(other.bulgeTarget, b.bulgeTarget);
                  PVector median = PVector.lerp(other.bulgeTarget, b.bulgeTarget, 0.5f);
                  if (d < knob10) {

                    b.collision = true;
                    strokeWeight(1);
                    stroke(b.lineColor);
                    noFill();
                    triangle(other.bulgeTarget.x, other.bulgeTarget.y, b.bulgeTarget.x, b.bulgeTarget.y, median.x, median.y);
                    noStroke();
                    fill(b.circleColor);
                    ellipse(median.x, median.y, d/5, d/5);

                    if(b.collision){
                      if(random(1) < 0.0002f) sprites.add(new Sprite(median.x, median.y, random(-0.5f, 0.5f), random(-0.5f, 0.5f), random(-0.01f, 0.01f), random(-0.01f, 0.01f), d/5));
                    }
            } else {
              b.collision = false;
            }
          }
        }
      }
    }

}

  public void addBand(){
    int c = colors[(int)random(8)];

    pitch = (int)map(pitch, 37, 90, 0, width);

    bands.add(new Band(pitch, 0, knob7, c, knob6, knob9, knob8));
  }

  public void keyAddBand(){
    int c = colors[(int)random(8)];

    bands.add(new Band(random(width), 0, knob7, c, knob6, knob9, knob8));
  }


  public void runSprites(){

    for (int i= sprites.size()-1; i>= 0; i--){
      Sprite s = sprites.get(i);
      s.run();
      if (s.isDead()) {
        sprites.remove(i);
      }
    }

  }

}
class Sketch2 {

  ArrayList<Shard> shards;
  ArrayList<CircleRunner> circleRunners;

  Sketch2(){

    shards = new ArrayList<Shard>();
    circleRunners = new ArrayList<CircleRunner>();
  }

  public void run(){

    runCircleRunners();
    createShard();
    runShards();

    println(frameRate, circleRunners.size());
  }

  public void runCircleRunners(){
    for (int i = circleRunners.size()-1; i > 0; i--) {
      CircleRunner c = circleRunners.get(i);
      c.run();
      if (c.isDead()) {
        circleRunners.remove(i);
      }
    }

    if(circleRunners.size() > 500){
      circleRunners.remove(circleRunners.size()-1);
    }

    if(circleRunners.size() > 1){
      CircleRunner c = circleRunners.get(circleRunners.size()-1);

      if(noteOn){

      }
    }

  }



  public void createShard(){

      for (int i = 0; i < circleRunners.size(); i++) {
        for (int j = 0; j < circleRunners.size(); j++) {
          if (i != j) {
            CircleRunner c = circleRunners.get(i);
            CircleRunner other = circleRunners.get(j);

            if(c != other){
              float d = PVector.dist(other.locations[0], c.locations[0]);
              if (d < knob10) {
                if(random(1) < 0.00012f) shards.add(new Shard(c.locations[0].x, c.locations[0].y));
              }
          }
        }
      }
    }

}

  public void addCircleRunner(){
    int c = colors[(int)random(8)];

    pitch = (int)map(pitch, 37, 90, 0, width);

    circleRunners.add(new CircleRunner(random(width), random(height), 1, c));
  }

  public void keyAddBand(){
    int c = colors[(int)random(8)];

    circleRunners.add(new CircleRunner(random(width), random(height), 1, c));
  }


  public void runShards(){

    for (int i= shards.size()-1; i>= 0; i--){
      Shard s = shards.get(i);
      s.run();
      if (s.isDead()) {
        shards.remove(i);
      }


    }

  }

}
class Sprite extends Particle{

  float size = random(10, 25);
  float rectWidth = random(2, 10);
  float rotz = random(0.002f, 0.02f);
  float roty = random(0.002f, 0.02f);

  int co = color(random(255), random(255), random(255));

  int mode;

  Sprite(float x, float y, float vx, float vy, float ax, float ay, float size){
    super(x, y, new PVector(vx, vy), new PVector(ax, ay), 255);
    this.co = colors[(int)random(8)];
    this.size = size;
    lifespan = 255.0f;

    mode = (int)random(1,3);
  }

  public void run(){
    move();
    update();
    display();
  }

  public void move(){
    location.add(velocity);
  }

  public void update(){
    lifespan -= 0.2f;
  }

  public void display(){



    if(mode == 1) createBox();
    if (mode == 2) createRect();
    createRect();

  }

  public void createBox(){
    pushMatrix();
    translate(location.x, location.y);
    rotateZ(frameCount * rotz);
    rotateY(frameCount * roty);
    box(size);
    popMatrix();
  }

  public void createRect(){
    rectMode(CENTER);
    rect(location.x, location.y, rectWidth, size*2);
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
