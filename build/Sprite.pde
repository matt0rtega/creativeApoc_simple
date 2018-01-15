class Sprite extends Particle{

  float size = random(10, 25);
  float rectWidth = random(2, 10);
  float rotz = random(0.002, 0.02);
  float roty = random(0.002, 0.02);

  color co = color(random(255), random(255), random(255));

  int mode;

  Sprite(float x, float y, float vx, float vy, float ax, float ay, float size){
    super(x, y, new PVector(vx, vy), new PVector(ax, ay), 255);
    this.co = colors[(int)random(8)];
    this.size = size;
    lifespan = 255.0;

    mode = (int)random(1,3);
  }

  void run(){
    move();
    update();
    display();
  }

  void move(){
    location.add(velocity);
  }

  void update(){
    lifespan -= 0.2;
  }

  void display(){



    if(mode == 1) createBox();
    if (mode == 2) createRect();
    createRect();

  }

  void createBox(){
    pushMatrix();
    translate(location.x, location.y);
    rotateZ(time * rotz);
    rotateY(time * roty);
    noStroke();
    box(size);
    popMatrix();
  }

  void createRect(){
    rectMode(CENTER);
    rect(location.x, location.y, rectWidth, size*2);
  }
}