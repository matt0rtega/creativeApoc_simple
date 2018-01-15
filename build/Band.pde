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

  color co, lineColor, circleColor;

  float strength;

  Band (float x, float y, float bulgeSize, color colorValue, float range, float theta, int mode) {
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


    lifespan = 255.0;

    x1 = -bandwidth;
    x2 = bandwidth;
  }

  void run() {
    update();
    display();
  }

  void update() {

    float d = rangeTarget - range;
    range += d * 0.02;

    lifespan -= 0.5;
  }

  void showTarget() {
    pushMatrix();
    ellipseMode(CENTER);
    ellipse(0, bulgeTarget.y, bulgeSize, bulgeSize);
    noStroke();
    fill(co, lifespan);
    popMatrix();
  }

  void display() {

    bulgeTarget.add(velocity);

    pushMatrix();

    if (polyType == 1) {
      beginShape(LINES);
    }
    if (polyType == 2) beginShape(TRIANGLES);
    if (polyType == 3) beginShape(TRIANGLE_STRIP);
    if (polyType == 4) { 
      beginShape(POINTS);
    }

    for (int y=-height/2; y<=height+height/2; y+=20) {

      float offsetx = map(noise(frameCount * 0.002 + (y * theta) + thetaOffset), 0, 1, 0, range);

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

  void colorSetup(int index) {
    float red = map(sin(frameCount * 0.002 + (index * theta * 0.01)), -1, 1, 0, red(co));
    float green = map(cos(frameCount * 0.001 + (index * theta * 0.01)), -1, 1, 0, green(co));
    float blue = map(sin(frameCount * 0.003 + (index * theta * 0.01)), -1, 1, 0, blue(co));

    color gradient = color(red, green, blue);

    fill(co, lifespan);

    switch(mode) {
    case 1:
      noStroke();
      if (polyType == 1) { 
        strokeWeight(10); 
        stroke(co, lifespan);
      }
      fill(co, lifespan);
      break;
    case 2:
      noFill();
      strokeWeight(1);
      stroke(co, lifespan);
      break;
    case 3:

      if (polyType == 4) { 
        strokeWeight(10); 
        stroke(co, lifespan);
      } else {
        strokeWeight(1);
        stroke(0, lifespan);
      }
      fill(co, lifespan);

      break;
    }
  }
}