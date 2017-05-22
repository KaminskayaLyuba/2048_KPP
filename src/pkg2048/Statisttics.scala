package pkg2048

import java.io._;
import scala.collection.mutable.MutableList;

class Statistics {

  def main (): Unit =
  {
    var i=0;
    var st: Array[Int] = Array[Int](0,0,0,0);
    var timeout : Long = System.currentTimeMillis();
    var a: MutableList[Int] = MutableList();
    var files : Array[Int] = new Array[Int](10000)


    var sum = 0;
    while (i<10000)
    {
      var filename = new String();
      filename="saved"+Integer.toString(i)+".txt";
      val e:IOException = new IOException
      var fis_step = new FileInputStream(filename);
      var inStream_step = new ObjectInputStream(fis_step);

      files(i) = inStream_step.available()/4;
      sum+=files(i)/Integer.BYTES;
      var j=0;
      while (j<files(i)/Integer.BYTES)
      {
        var t = inStream_step.readInt();
        a += t;
        j+=1;
      }
      if (a.isEmpty) System.out.print("\nempty")
      inStream_step.close();
      fis_step.close();
      i+=1;

    }
    a.foreach { x => st(x)+=1;}
    timeout = System.currentTimeMillis() - timeout;
    System.out.print(" \nScala STAT:");
    System.out.print(timeout);
  }

}/**
  * Created by люба on 22.05.2017.
  */

