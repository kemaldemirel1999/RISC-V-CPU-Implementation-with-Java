import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Islemci {
	/*Yazmac Obegi*/
	/*RISC-V I yazmaclari*/
	 public int[] my_registers = new int[32];
	 public int frequency;
	 public double CPI_R,CPI_I,CPI_B,CPI_S,CPI_J;
	 public int numberOfCycle = 0;
	 public int programCounter = 0;
	 public double executionTime;
	 public int numberOfInstructions = 0;
	
	 ArrayList<String> commands = new ArrayList<String>();
	 HashMap<Integer,String> instructionMemory = new HashMap<Integer,String>();
	 HashMap<Integer,Integer> dataMemory = new HashMap<Integer,Integer>();
	public Islemci(String filename1,String filename2)
	{
		loadFile(filename1,filename2);
		save();
	}
	public String hexToBinary(String hex)
	{
		String binaryNum = "";
		HashMap<Character,String> hash = new HashMap<Character,String>();
		hash.put('A', "1010"); 
		hash.put('B', "1011"); 
		hash.put('C', "1100"); 
		hash.put('D', "1101"); 
		hash.put('E', "1110"); 
		hash.put('F', "1111");
		hash.put('0', "0000"); 
		hash.put('1', "0001"); 
		hash.put('2', "0010"); 
		hash.put('3', "0011"); 
		hash.put('4', "0100"); 
		hash.put('5', "0101"); 
		hash.put('6', "0110"); 
		hash.put('7', "0111"); 
		hash.put('8', "1000"); 
		hash.put('9', "1001");
		hex = hex.toUpperCase();
		if(hex.contains("x") || hex.contains("X"))	hex = hex.substring(hex.indexOf("X")+1);
		/*hash a atarak uygun hexadecimala denk gelen 2lik tabandaki 4 bitlik sayiyi buluruz.*/
		/*Daha sonra bunlari stringe ekleriz.*/
		for(int i=0; i<hex.length(); i++)
		{
			binaryNum += hash.get(hex.charAt(i));
		}
		return binaryNum;
	}
	public  int binaryToDecimal(String binary)
	{
		try
		{
			int decimal = Integer.parseInt(binary,2);
			return decimal;
		}catch(Exception e)
		{
			e.printStackTrace();
			return (-1);
		}
	}
	public  int hexToDecimal(String hex)
	{
		if(hex.contains("-"))
		{
			hex = hex.substring(1);
			String binary = hexToBinary(hex);
			int decimal = binaryToDecimal(binary);
			return decimal*(-1);
		}
		String binary = hexToBinary(hex);
		return binaryToDecimal(binary);
	}
	/*yazmac obegindeki veriyi dosyaya yazdirir.*/
	public  void save()
	{
		PrintWriter outputStream = null;
		try
		{
			outputStream = new PrintWriter("cikti.txt");
			for(int i=0; i<1024; i++)
			{
				String currentJob = instructionMemory.get(programCounter);
				if( !(currentJob.contains("x") || currentJob.contains("X") ))
				{
					continue;
				}
				String instruction_name = currentJob.substring(currentJob.indexOf(" ")+1);
				if(instruction_name.equals("SON"))
				{
					break;
				}
				instruction_name = instruction_name.substring(0,instruction_name.indexOf(" "));
				if(instruction_name.equals("add") || instruction_name.equals("sub") || instruction_name.equals("and") || instruction_name.equals("srl") || 
						instruction_name.equals("sra") || instruction_name.equals("xor"))
				{
					itIsRType(instructionMemory.get(programCounter));
					numberOfCycle += CPI_R;
				}
				else if(instruction_name.equals("addi") || instruction_name.equals("subi") || instruction_name.equals("xori") || instruction_name.equals("srli") || 
						instruction_name.equals("srai") || instruction_name.equals("lw") || instruction_name.equals("lb"))
				{
					itIsIType(instructionMemory.get(programCounter));
					numberOfCycle += CPI_I;
				}
				else if(instruction_name.equals("jal") || instruction_name.equals("jalr") )
				{
					itIsJType(instructionMemory.get(programCounter));
					numberOfCycle += CPI_J;
				}
				else if(instruction_name.equals("beg") || instruction_name.equals("bge") || instruction_name.equals("blt") || instruction_name.equals("sw") || 
						instruction_name.equals("sb") )
				{
					itIsBSType(instructionMemory.get(programCounter));
				}
				numberOfInstructions++;
			}
			double s =  (1/( frequency*(Math.pow(10, 6)) ));
			double CPI = ((double)numberOfCycle/(double)numberOfInstructions);
			executionTime = numberOfInstructions * CPI * s;
			for(int i=0; i<32; i++)
			{
				outputStream.println("x"+i+" : " + my_registers[i]);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		outputStream.close();
	}
	public  void loadFile(String filename,String filename2)
	{
		Scanner inputStream,inputStream2 = null;
		try
		{
			inputStream = new Scanner(new FileInputStream(filename));
			inputStream2 = new Scanner(new FileInputStream(filename2));
			while(inputStream.hasNextLine())
			{
				commands.add(inputStream.nextLine());
			}
			while(inputStream2.hasNextLine())
			{
				String line = inputStream2.nextLine();
				String temp = line.substring(0,line.indexOf(" "));
				int temp2 = Integer.parseInt(line.substring(line.indexOf(" ")+1));
				if(temp.equals("Frekans"))	frequency = temp2;
				else if(temp.equals("R"))	CPI_R = temp2;
				else if(temp.equals("I"))	CPI_I = temp2;
				else if(temp.equals("B"))	CPI_B = temp2;
				else if(temp.equals("S"))	CPI_S = temp2;
				else if(temp.equals("J"))	CPI_J = temp2;
			}
			inputStream.close();
			inputStream2.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		for(int i=0; i<commands.size(); i++)
		{
			if( !(commands.get(i).contains("x") || commands.get(i).contains("X") ))
			{
				continue;
			}
			Integer address = hexToDecimal(commands.get(i).substring(0,commands.get(i).indexOf(" ")));
			String instructionData = commands.get(i);
			instructionMemory.put(address, instructionData);
		}
		for(int i=0; i<=1048576; i=i+4)
		{
			dataMemory.put(i,0); 
		}

	}
	public  void itIsRType(String R)
	{
		int counter = hexToDecimal(R.substring(0,R.indexOf(" ")));
		R = R.substring(R.indexOf(" ")+1);
		String instruction_name = R.substring(0,R.indexOf(" "));
		R = R.substring(R.indexOf(" ") + 1);
		int hy = Integer.parseInt(R.substring(R.indexOf("x")+1,R.indexOf(" ")));
		R = R.substring(R.indexOf(" ")+1);
		int ky1 = Integer.parseInt(R.substring(R.indexOf("x")+1,R.indexOf(" ")));
		R = R.substring(R.indexOf(" ")+1);
		int  ky2 = Integer.parseInt(R.substring(R.indexOf("x")+1));
		switch(instruction_name)
		{
		case "add":
			my_registers[hy] = my_registers[ky1] + my_registers[ky2];
			break;
		case "sub":
			my_registers[hy] = my_registers[ky1] - my_registers[ky2];
			break;
		case "and":
			my_registers[hy] = my_registers[ky1] & my_registers[ky2];
			break;
		case "srl":
			my_registers[hy] = my_registers[ky1] >> my_registers[ky2];
			break;
		case "sra":
			if(my_registers[ky1] >= 0) 
			{
				my_registers[hy] = my_registers[ky1] >> my_registers[ky2];
			}
			else if(my_registers[ky1] < 0) 
			{
				String temp = "";
				int i;
				for(i=0 ;i<my_registers[ky2]; i++)
				{
					temp += "1";
				}
				for(; i<32; i++)
				{
					temp += "0";
				}
				my_registers[hy] = my_registers[ky1] >> my_registers[ky2];
				my_registers[hy] = my_registers[hy] | Integer.parseInt(temp,2);
			}
			break;
		case "xor":
			my_registers[hy] = my_registers[ky1] ^ my_registers[ky2];
			break;			
		default:
			
			break;
		}
		programCounter += 4;
	}
	public  void itIsIType(String R)
	{
		int counter = hexToDecimal(R.substring(0,R.indexOf(" ")));
		R = R.substring(R.indexOf(" ")+1);
		String instruction_name = R.substring(0,R.indexOf(" "));
		R = R.substring(R.indexOf(" ") + 1);
		int hy = Integer.parseInt(R.substring(R.indexOf("x")+1,R.indexOf(" ")));
		R = R.substring(R.indexOf(" ")+1);
		int ky1 = Integer.parseInt(R.substring(R.indexOf("x")+1,R.indexOf(" ")));
		R = R.substring(R.indexOf(" ")+1);
		int  immediateValue = hexToDecimal(R);
		switch(instruction_name)
		{
		case "addi":
			my_registers[hy] = my_registers[ky1] + immediateValue;
			break;
		case "subi":
			my_registers[hy] = my_registers[ky1] - immediateValue;
			break;
		case "slti":
			if(my_registers[ky1] < immediateValue)		my_registers[hy] = 1;
			else										my_registers[hy] = 0;
			break;
		case "srai":
			if(my_registers[ky1] >= 0) 
			{
				my_registers[hy] = my_registers[ky1] >> immediateValue;
			}
			else if(my_registers[ky1] < 0) 
			{
				String temp = "";
				int i;
				for(i=0 ;i<immediateValue; i++)
				{
					temp += "1";
				}
				for(; i<32; i++)
				{
					temp += "0";
				}
				my_registers[hy] = my_registers[ky1] >> immediateValue;
				my_registers[hy] = my_registers[hy] | Integer.parseInt(temp,2);
			}
			break;
		case "xori":
			my_registers[hy] = my_registers[ky1] ^ immediateValue;
			break;
		case "lw":
			my_registers[hy] = dataMemory.get(my_registers[ky1] + immediateValue);
			break;
		case "lb":
			my_registers[hy] = Integer.parseInt("00000000000000000000000011111111",2) & my_registers[my_registers[ky1] + immediateValue/4];
			break;
		default:
			
			break;
		}
		programCounter += 4;
	}
	public  void itIsJType(String R)
	{
		int address = hexToDecimal(R.substring(0,R.indexOf(" ")));
		R = R.substring(R.indexOf(" ")+1);
		String instruction_name = R.substring(0,R.indexOf(" "));
		R = R.substring(R.indexOf(" ") + 1);
		int hy = Integer.parseInt(R.substring(R.indexOf("x")+1,R.indexOf(" ")));
		int immediateValue,ky = 4;
		if(!instruction_name.equals("jalr")) 
		{
			R = R.substring(R.indexOf(" ")+1);
			immediateValue = hexToDecimal(R);
		}
		else 
		{
			R = R.substring(R.indexOf(" ")+1);
			ky = Integer.parseInt(R.substring(R.indexOf("x")+1,R.indexOf(" ")));
			R = R.substring(R.indexOf(" ")+1);
			immediateValue = hexToDecimal(R);
		}
		switch(instruction_name)
		{
		case "jal":
			my_registers[hy] = address + 4;
			programCounter += immediateValue*2;	
			break;
		case "jalr":
			my_registers[hy] = address + 4;
			programCounter = my_registers[ky] + immediateValue*2;
			break;			
		default:
			
			break;
		}
	}
	public  void itIsBSType(String R)
	{
		int counter = hexToDecimal(R.substring(0,R.indexOf(" ")));
		R = R.substring(R.indexOf(" ")+1);
		String instruction_name = R.substring(0,R.indexOf(" "));
		R = R.substring(R.indexOf(" ") + 1);
		int ky1 = Integer.parseInt(R.substring(R.indexOf("x")+1,R.indexOf(" ")));
		R = R.substring(R.indexOf(" ")+1);
		int ky2 = Integer.parseInt(R.substring(R.indexOf("x")+1,R.indexOf(" ")));
		R = R.substring(R.indexOf(" ")+1);
		int  immediateValue = hexToDecimal(R);
		switch(instruction_name)
		{
		case "beg":
			if(my_registers[ky1] == my_registers[ky2])	programCounter += immediateValue*2;
			else										programCounter += 4;
			numberOfCycle += CPI_B;
			break;
		case "bge":
			if(my_registers[ky1] >= my_registers[ky2])	programCounter += immediateValue*2;
			else										programCounter += 4;
			numberOfCycle += CPI_B;
			break;
		case "blt":
			if(my_registers[ky1] < my_registers[ky2])	programCounter += immediateValue*2;
			else										programCounter += 4;
			numberOfCycle += CPI_B;
			break;
		case "sw":
			dataMemory.put(my_registers[ky1] + immediateValue , my_registers[ky2]);
			programCounter += 4;
			numberOfCycle += CPI_S;
			break;
		case "sb":
			int x = Integer.parseInt("00000000000000000000000011111111",2);
			dataMemory.put(my_registers[ky1] + immediateValue , my_registers[ky2] & x);
			programCounter += 4;
			numberOfCycle += CPI_S;
			break;		
		default:
			
			break;
		}
	}
}
