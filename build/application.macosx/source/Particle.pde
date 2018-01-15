class Particle {
  PVector location;
  PVector velocity;
  PVector acceleration;
  float lifespan;

  Particle(float x, float y) {
    location = new PVector(x, y);
    velocity = new PVector(5, 0);
    acceleration = new PVector(0, 0);
    lifespan = 255.0;
  }

  void run() {
    update();
    display();
  }

  // Method to update position
  void update() {
    //location.add(velocity);
    //velocity.add(acceleration);
    lifespan -= 1.0;
  }

  void display() {
    stroke(0, lifespan);
    fill(175, 200, lifespan);
    ellipse(location.x, location.y, 8, 8);
  }

  // Is the particle still useful?
  boolean isDead() {
    if (lifespan<0) {
      return true;
    } else {
      return false;
    }
  }
}