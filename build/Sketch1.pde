class Sketch1 {

  ArrayList<Band> bands;
  ArrayList<Sprite> sprites;

  Sketch1(){

    bands = new ArrayList<Band>();
    sprites = new ArrayList<Sprite>();
  }

  void run(){
    lights();
    directionalLight(255, 0, 255, mouseX, mouseY, mouseY);

    runBands();
    //runSprites();
  }

  void runBands(){
    for (int i = bands.size()-1; i > 0; i--) {
      Band b = bands.get(i);
      b.run();
      if (b.isDead() || bands.size() > 100) {
        bands.remove(i);
        println(bands.size());
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



  void createCircles(){

      for (int i = 0; i < bands.size(); i++) {
        for (int j = 0; j < bands.size(); j++) {
          if (i != j) {
            Band b = bands.get(i);
            Band other = bands.get(j);

            if(b != other){
              float d = PVector.dist(other.bulgeTarget, b.bulgeTarget);
                  PVector median = PVector.lerp(other.bulgeTarget, b.bulgeTarget, 0.5);
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
                      if(random(1) < 0.0002) sprites.add(new Sprite(median.x, median.y, random(-0.5, 0.5), random(-0.5, 0.5), random(-0.01, 0.01), random(-0.01, 0.01), d/5));
                    }
            } else {
              b.collision = false;
            }
          }
        }
      }
    }

}

  void addBand(){
    color c = colors[(int)random(8)];

    pitch = (int)map(pitch, 37, 90, 0, width);

    bands.add(new Band(pitch, 0, knob7, c, knob6, knob9, knob8));
  }

  void keyAddBand(){
    color c = colors[(int)random(8)];

    bands.add(new Band(random(width), 0, knob7, c, knob6, knob9, knob8));
  }


  void runSprites(){

    for (int i= sprites.size()-1; i>= 0; i--){
      Sprite s = sprites.get(i);
      s.run();
      if (s.isDead() || sprites.size() > 20) {
        sprites.remove(i);
        println(sprites.size());
      }
    }

  }

}