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

  color co;

  float strength;

  Band (float x, float y, float bulgeSize, color colorValue, float range, float theta, int mode){
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
    if(random(1) < 0.2) polyType = (int)random(1, 5);

    co = colorValue;

    x1 = -bandwidth;
    x2 = bandwidth;
  }

  void run(){
    update();
    display();
  }

  void update(){
    float d = rangeTarget - range;
    range += d * 0.02;

    lifespan -= 0.2;
  }

  void showTarget(){
    pushMatrix();
    translate(location.x, location.y);

    ellipseMode(CENTER);
    ellipse(0, bulgeTarget.y, bulgeSize, bulgeSize);
    noStroke();
    fill(co, lifespan);
    popMatrix();
  }

  void display(){

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

      float offsetx = map(noise(frameCount * 0.002 + (y * theta) + thetaOffset), -1, 1, 0, range);

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