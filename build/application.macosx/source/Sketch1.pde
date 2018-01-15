class Sketch1 {

  ArrayList<Band> bands;

  Sketch1(){

    bands = new ArrayList<Band>();
  }

  void run(){
    update();
  }

  void update(){
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

  void addBand(){
    color c = colors[(int)random(8)];

    pitch = (int)map(pitch, 37, 90, 0, width);

    bands.add(new Band(pitch, 0, cfsize, c, cfrange, numbox1 / 10, radioButton1));
  }
  
  void keyAddBand(){
    color c = colors[(int)random(8)];

    pitch = (int)map(pitch, 37, 90, 0, width);

    bands.add(new Band(random(width), 0, cfsize, c, cfrange, numbox1 / 10, radioButton1));
  }


}