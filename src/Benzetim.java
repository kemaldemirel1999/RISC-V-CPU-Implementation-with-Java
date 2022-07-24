import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Benzetim 
{

	public static void main(String[] args)
	{
		PrintWriter file = null;
		try
		{
			file = new PrintWriter("sonuclar.txt");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if(args.length == 2)
		{
			Islemci islemci1 = new Islemci(args[0],args[1]);
			System.out.println("Toplam Buyruk Sayisi:"+islemci1.numberOfInstructions);
			file.println("Toplam Buyruk Sayisi:"+islemci1.numberOfInstructions);
			System.out.println("Toplam Cevrim Sayisi:"+islemci1.numberOfCycle);
			file.println("Toplam Cevrim Sayisi:"+islemci1.numberOfCycle);
			System.out.printf("Yurutme Zamani:%.12f\n",islemci1.executionTime);
			file.printf("Yurutme Zamani:%.12f\n",islemci1.executionTime);
		}
		else
		{
			Islemci islemci1 = new Islemci(args[0],args[1]);
			Islemci islemci2 = new Islemci(args[0],args[2]);
			double ratio = (islemci1.executionTime) / (islemci2.executionTime);
			if(ratio > 1)
			{
				System.out.println("Islemci1'in basarimi Islemci2'nin basarimindan "+ratio+" kat daha yuksek.");
			}
			else if(ratio < 1)
			{
				System.out.println("Islemci1'in basarimi Islemci2'nin basarimindan "+ratio+" kat daha yavas.");
			}
			if(ratio == 1)
			{
				System.out.println("Islemci1'in basarimi Islemci2'nin basarimi ile esit");
			}
		}
		file.close();
	}
}
