package PracaDyplomowa.MichalNguyen.PracaDyplomowaAplikacja.Enigma;

import static java.lang.Character.toLowerCase;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")

public class Enigma 
{
	
	public static final int Nchars = 69;  
	// Calkowita liczba szyfrowanlnych znakow 
	public static final int Mchars = 70;  
	// Buffer do przetrzymywania znakow 
	public static final int Nrotors = 11; 
	// Maksymalna liczba dostepnych wirnikow/rotorow (1 - bazowy: wirniki 1 -> 10)
	public static final int Nrefls = 5;   
	// Calkowita liczba dostepnych reflektorow (1-bazowy: 1-4)
	public static final int Nsteps = 11;  
	// Maksymalna liczba krokow szyfracji
	// = 2*4 (wirniki) + 2 (lacznica kablowa) + 1 (reflektor)
	public static final int  Nline = 1024; 
	public static final String NULL = null;
	
	public static int liczbaWirnikow;  													// liczba wirnikow/rotorow umiszczonych w enigmie 
																						// (min 1 max 4)
	public static int liczbaKrokow;   													// aktualna liczba krokow szyfrowania
																						// = 2*liczbaWirnikow + 2 (lacznicaKablowa) + 1 (reflektor)
	public static int[] pozWirnikow = new int [Nrotors];     							// Pozycja wirnika
	public static char[] okno = new char [Nrotors];    									// Znak w oknie 
	public static char[] oknoWartosc = new char [Nrotors];   							// wartosć wstepna w oknie
	public static String[] uzwojeniaWirnika = new String [Nrotors]; 					// Uzwojenia wirnika
	public static char[] wciecieWirnika = new char [Nrotors];   						// Pozucja wciecia w rotorze
	public static int[] typWirnika = new int [Nrotors];  								// Numer rotora (t,1-8,b,g)
	public static String reflektor ;            										// Uzwojenia na reflektorze
	public static char[] lacznicaKablowa = new char [Mchars];   						// Uzwojenia na lacznicy kablowej
	public static int typReflektora;              										// Typ uzytego relfektora
	public static char[] kroki = new char [Nsteps];        								// Tablica przechowujaca kroki szyfrowania
	
	public static char[] linaWejsciowa = new char [Nline];   							// lina wejsciowa pliku 
	public static char[] linaWyjsciowa = new char [Nline];  							// linia wyjsciowa pliku
	
	//funkcaj uruchamiajaca enigme
	public static void startEnigma (String PlikWejsciowy, String PlikWyjsciowy, String lokalizacjaLog, String NazwaPliku) throws IOException
	{
		String Log = lokalizacjaLog + NazwaPliku;
		
		UruchomEnigma.UstawieniaDomyslne();
		UstawieniaUzytkownika.SprawdzUstawieniaUzytkownika();
		
		reset();
		SzyfrowaniePliku.OdczytPliku(PlikWejsciowy, PlikWyjsciowy, Log);
		reset();
		
		
	}
	public static void reset()
	{
		for (int i = 1; i <= liczbaWirnikow; ++i)
			okno[i] = oknoWartosc[i];
	}
	
	public static class ElementyEnigmy
	{
		public static String[] WIRNIKI  // uzwojenia rotorow
				= {
					// alfabet wejsciowy ("rotor" 0, jest nie uzywany)
					"abcdefghijklmnopqrstuvwxyz0123456789.,:; ()[]'\"-+/*&~`!@#$%^_={}|\\<>?",
					// rotor 1
					"ekmflgdqvzntowyhxuspaibrcj4.:5,63)-&;' +*7/\"](081[29?><\\|}{=^_%$#@!`~",
					// rotor 2
					"ajdksiruxblhwtmcqgznpyfvoe093.]8[\"/1,7+':2)6&;(*5- 4?><\\|}{=^_%$#@!`~",
					// rotor 3
					"bdfhjlcprtxvznyeiwgakmusqo13579,2(['/-&;*48+60.:\"]) ?><\\|}{=^_%$#@!`~",
					// rotor 4
					"esovpzjayquirhxlnftgkdcmwb4] -(&90*)\"8[7/,;5'6.32:+1?><\\|}{=^_%$#@!`~",
					// rotor 5
					"vzbrgityupsdnhlxawmjqofeck-&1[68'*\"(]3;7,/0+:9) 542.?><\\|}{=^_%$#@!`~",
					// rotor 6
					"jpgvoumfyqbenhzrdkasxlictw9(6- \":5*)14;7&[3.0]/,82'+?><\\|}{=^_%$#@!`~",
					// rotor 7
					"nzjhgrcxmyswboufaivlpekqdt;&976[2/:*]+1 \"508-,(4.)3'?><\\|}{=^_%$#@!`~",
					// rotor 8
					"fkqhtlxocbjspdzramewniuygv5.)7',/ 219](3&[0:4+;8\"*6-?><\\|}{=^_%$#@!`~",
					// beta rotor
					"leyjvcnixwpbqmdrtakzgfuhos,4*9-2;8/+(1):3['0.&65\"7 ]?><\\|}{=^_%$#@!`~",
					// gamma rotor
					"fsokanuerhmbtiycwlqpzxvgjd5] .0;\"4[7:1'8*2+,)(&/-693?><\\|}{=^_%$#@!`~"
				};
		//tablica zawierajaca wciencia na rotorach
		public static char[] WIRNIKI_WCIECIE = { 'z', 'q', 'e', 'v', 'j', 'z', 'z', 'z', 'z', 'a', 'a' };
		
		public static String[] REFLEKTOR  // Reflektory
				= {
					// alfabet wejsciowy ("REFLEKTOR" 0 jest nie uzywany)
					"abcdefghijklmnopqrstuvwxyz0123456789.,:; ()[]'\"-+/*&~`!@#$%^_={}|\\<>?",
					// reflektor B, thick
					"yruhqsldpxngokmiebfzcwvjat*[\"7)],3(/;6 .:8415&2+-90'?<>\\|}{=_^%$#@`!~",
					// reflektor C, thick
					"fvpjiaoyedrzxwgctkuqsbnmhl5-(980 *43[&/+62'.\")]1;:7,?<>\\|}{=_^%$#@`!~",
					// reflektor B, dunn
					"enkqeuywjicopblmdxzvfthrgs4;.)0\"*+982 (1,:3/&-5'7[6]?<>\\|}{=_^%$#@`!~",
					// reflektor C, dunn
					"rdobjntkvehmlfcwzrxgyipsuq[3 19;'.-47:,52+&0/6*8(]\")?<>\\|}{=_^%$#@`!~"
				};
		
		public static String LACZNICA_KABLOWA  // Domyslne uzwojenia lacznicy kablowej 
		="abcdefghijklmnopqrstuvwxyz0123456789.,:; ()[]'\"-+/*&~`!@#$%^_={}|\\<>?";
		
		public static String alfabet  // alfabet wejsciowy
		="abcdefghijklmnopqrstuvwxyz0123456789.,:; ()[]'\"-+/*&~`!@#$%^_={}|\\<>?";
		
	}
	//clasa inicjujaca enigme do ustawien domyslnych
	public static class UruchomEnigma
	{
		UruchomEnigma()
		{
			
		}
		
		static void UstawieniaDomyslne()
		{
			liczbaWirnikow = 4;
			liczbaKrokow = (liczbaWirnikow << 1) + 3;
			String pb = ElementyEnigmy.LACZNICA_KABLOWA;
			for (int j = 0; j < pb.length(); j++)
			{
				lacznicaKablowa [j]= pb.charAt(j);
			};
			for (int i = 0; i <= liczbaWirnikow; ++i) 
			{
				uzwojeniaWirnika[i] = ElementyEnigmy.WIRNIKI[i];
				wciecieWirnika[i] = ElementyEnigmy.WIRNIKI_WCIECIE[i];
				typWirnika[i] = i;
				oknoWartosc[i] = okno[i] = 'b';
			}
			reflektor = ElementyEnigmy.REFLEKTOR[1];
			typReflektora = 1;
		}
	}
	//Klasa odpowiadajaca za ustawinie enigmy do ustawien uzytkownika jezeli takie istnieja
	public static class UstawieniaUzytkownika
	{
		private static File file = null;
		private static FileReader inFp = null;
		
		UstawieniaUzytkownika()
		{
			
		}
		//funkja odczytujaca plik esetup
		static void SprawdzUstawieniaUzytkownika() throws IOException
		{
			String sciezkaPliku = "E:\\PracaDyplomowa\\PracaDyplomowaAplikacja\\UstawieniaUzytkownika\\esetup";
			file = new File (sciezkaPliku);
			
			if (file.exists())
				if(file.length()!=0)
				{
					inFp =  new FileReader(sciezkaPliku);
					BufferedReader in = new BufferedReader(inFp, Nline);
					String cLinie;
					List<String> lista = new ArrayList<String>();
					while ((cLinie = in.readLine()) != null) 
					{
						lista.add(cLinie);
					}
					String[] stringTablica = lista.toArray(new String[0]);
					UstawLaczniceKablowa(stringTablica);
					UstawWirnikiReflektor(stringTablica);
					inFp.close();
				}
		}
		
		
		private static void UstawLaczniceKablowa(String [] plikKonfiguracyjny) throws IOException // tworzenie par liter na lacznicy kablowej
		{
			int i, n, x;
			char p1, p2, ch;

			
			// Odczytywanie lini tekstu z pliku odpowaidajacej za pary liter polaczonych na lacznicy kablowej.
			// Dlugosc lini ma byc liczba parzysta
			
			String pKablowa;
			pKablowa = plikKonfiguracyjny[0];
			
			
			n = pKablowa.length();
			
		    for(int j = 0; j < n; j++)
			{
				linaWejsciowa[j] = pKablowa.charAt(j);
			}
			
			for (i = 0; i < n; i += 2) {
				p1 = linaWejsciowa[i];
				p2 = linaWejsciowa[i + 1];
				x = indeks(p1);
				if ((ch = lacznicaKablowa[x]) != p1) { 		// jezeli dany znak jest zajety do go odlacza
					lacznicaKablowa[indeks(ch)] = ch;
					lacznicaKablowa[x] = p1;
				}
				lacznicaKablowa[x] = p2;                 	// zalaczenie znaku
				x = indeks(p2);
				if ((ch = lacznicaKablowa[x]) != p2) { 		// jezeli dany znak jest zajety do go odlacza
					lacznicaKablowa[indeks(ch)] = ch;
					lacznicaKablowa[x] = p1;
				}
				lacznicaKablowa[x] = p1;               	 	// zalaczenie znaku
			}
			ElementyEnigmy.LACZNICA_KABLOWA = new String(lacznicaKablowa);
		}
		
		private static int indeks(char c)
		{
			// znak c w alfabecie 

			int i = 0;

			while ((i < Nchars) && (c != ElementyEnigmy.alfabet.charAt(i)))
				++i;
			return i;
		}
		
		private static void UstawWirnikiReflektor(String [] plikKonfiguracyjny) throws IOException
		{
			int i, n, rotor, rotPos;
			char ch, ringPos, 
			chWirnik = ' ', 
			chReflektor = ' ';

			// Odczytywanie ilosci rotorow w Enigmie, 
			// ustalenie ilosci krokow szyfrowania 
			// utalenie typu, pozycji i rodzja znaku na wirniku
			//
			//   (a) rotor typ (1-8,b,g),
			//   (b) rotor pozycja (1-liczbaWirnikow),
			//   (c) znak na pierscieniu (pozucja).

			String lWirnik;
			int wielkoscPliku = plikKonfiguracyjny.length;
			lWirnik = plikKonfiguracyjny[1];
			
			for(int j = 0; j < lWirnik.length(); j++)
			{
				chWirnik = lWirnik.charAt(j);
			}
			
			liczbaWirnikow = ChrToInt(chWirnik);
			
			if (liczbaWirnikow > 4)
				liczbaWirnikow = 4;
			
			liczbaKrokow = (liczbaWirnikow << 1) + 3;
			String [] jWirnik = new String [liczbaWirnikow];
			
			for(int o = 2; o < (wielkoscPliku-1); o++) {
				int q = o - 2;
				jWirnik [q]= plikKonfiguracyjny[o];
				
			}
			for (i = 1; i <= liczbaWirnikow; ++i) {
				String usRotora = jWirnik[i-1];
				
			    for(int j = 0; j < usRotora.length(); j++)
				{
					linaWejsciowa[j] = usRotora.charAt(j);
				}
				ch = linaWejsciowa[0];
				if (Character.isDigit(ch))
					rotor = ChrToInt(ch);
				else {
					ch = Character.toLowerCase(ch);
					rotor = ch == 'b' ? 9
						: ch == 'g' ? 10 : 0;
				}
				rotPos = ChrToInt(linaWejsciowa[1]);
				ringPos = linaWejsciowa[2];
				oknoWartosc[rotPos] = okno[rotPos] = ringPos;
				UstawWirnik(rotPos, rotor);
				
			}
			

			// Odczytywanie lini z pliku esetup zawierającej typ reflektora
			String tReflektor = plikKonfiguracyjny[wielkoscPliku-1];
		    for(int j = 0;j<tReflektor.length();j++)
			{
				chReflektor = tReflektor.charAt(j);
			}
			ch = chReflektor;
			switch (ch) {
			case 't': n = 0; break;      case 'b': n = 1; break;
			case 'c': n = 2; break;      case 'B': n = 3; break;
			case 'C': n = 4; break;       default: n = 0; break;
			}
			reflektor = ElementyEnigmy.REFLEKTOR[n];
			typReflektora = n;
		}
		//funkcja zamieniajaca znak char na int 
		private static int ChrToInt(char c)
		{
			// '0' <= c <= '9'

			return (int)(c - '0');
		}
		
		private static void UstawWirnik(int position, int r)
		// ustawienie uzwojenia dla pojedynczego wirnika
		{
			uzwojeniaWirnika[position] = ElementyEnigmy.WIRNIKI[r];
			wciecieWirnika[position] = ElementyEnigmy.WIRNIKI_WCIECIE[r];
			typWirnika[position] = r;
		}

	}
	//clasa odpowiadajca za szyfrowanie pliku i utworzenie pliku zaszyfrowanego 
	public static class SzyfrowaniePliku
	{
		private static File file = null;
		private static FileReader inFp = null;
		private static RandomAccessFile outFp = null;
		private static RandomAccessFile logFp = null;
		
		SzyfrowaniePliku()
		{
			
		}
		//funkcja szyfrujaca plik i tworzaca plik zaszyfrowany 
		public static void OdczytPliku(String inFname, String encFname, String logFname) throws IOException
			{
			
			
				if (OtworzPliki(inFname, encFname, logFname)) 
				{
					UstawWirnikiNaPozycje();
					RaportUstawienEnigmy();
					SzyfrowanieTekstu();
					ZamknijPliki();
				}
			}
		//funkcaj otwierajaca i tworzaca pliki 
		private static boolean OtworzPliki(String inFname, String encFname, String logFname) throws FileNotFoundException
			{
				file = new File(inFname);
				inFp = new FileReader(inFname);
				outFp = new RandomAccessFile(encFname, "rw");
				logFp = new RandomAccessFile(logFname, "rw");
				
				return    (file.length( ) != 0);
			}
		//funkcaj ustawiajaca pozyjce rotorow
		private static void UstawWirnikiNaPozycje()
		{
			int i, j, k, m;
			char ch;
			char[] Rwiring = new char [Nchars];

			for (i = 1; i <= liczbaWirnikow; ++i) 
			{
				j = typWirnika[i];
				ch = okno[i];
				String RotorWiring = uzwojeniaWirnika[i];
				for(m = 0; m < RotorWiring.length(); m++)
					Rwiring[m] =  RotorWiring.charAt(m);
				k = 0;
				while (Rwiring[k] != ch)
					++k;
				pozWirnikow[j] = k;
			}
		}
		//funkcja zapisuajca do pliku log dane z enigmy
		private static void RaportUstawienEnigmy() throws IOException
		{
			logFp.seek(0);
			logFp.writeBytes("Plugboard mappings:\r\n");
			logFp.writeBytes(ElementyEnigmy.WIRNIKI[0] + "\r\n");
			logFp.writeBytes(ElementyEnigmy.LACZNICA_KABLOWA + "\r\n");
			logFp.writeBytes("\r\nRotor wirings:\r\n");
			logFp.writeBytes("position rotor ring setting notch sequence\r\n");
			for (int i = liczbaWirnikow; i >= 1; --i)
				logFp.writeBytes("       " + i + "     " + typWirnika[i] + "            " + okno[i] + "     " + wciecieWirnika[i] + " " + uzwojeniaWirnika[i] + "\r\n");
			logFp.writeBytes("\r\nreflector: " + " Type:" + typReflektora +" ["+ reflektor +"] "+"\r\n");
			logFp.writeBytes("\r\nrotors:\r\n");
			PokazWirniki();
			
		}
		//funkcja zapisujaca do pliku log rodzaje i uzwojania rotorow 
		private static void PokazWirniki() throws IOException
		{
			int i, j , k, m;
			char[] Rwiring = new char [Nchars];

			for (i = liczbaWirnikow; i >= 1; --i) 
			{
				
				logFp.writeBytes(i + ":" + " ");
				String RotorWiring = uzwojeniaWirnika[i];
				for(m = 0; m < RotorWiring.length(); m++)
					Rwiring[m] =  RotorWiring.charAt(m);
				k = pozWirnikow[i];
				for (j = 0; j < k; ++j)
					logFp.writeByte(Rwiring[j]);
				logFp.writeBytes("->");
				for (j = k; j < Nchars; ++j)
					logFp.writeByte(Rwiring[j]);
				logFp.writeBytes("\r\n");
				
			}
		}
		//funkcja szyfrujaca plik i zapisujaca wynik do pliku wyjsciowego
		private static void SzyfrowanieTekstu() throws IOException
		{
			int i, n;
			char c1, c2;
			logFp.writeBytes("\r\n\r\nEncryption\r\n");
			
			BufferedReader in = new BufferedReader(inFp, Nline);
			String rl = null;
			
			outFp.seek(0);
			while ((rl = in.readLine()) != NULL) 
			{
				
				n = rl.length();
				for(int j = 0; j < n; j++)
				{
					linaWejsciowa[j] = rl.charAt(j);
				}
				
				for (i = 0; i < n; ++i) 
				{
					c1 = linaWejsciowa[i];
					if (Character.isUpperCase((int)c1))
						c1 = Character.toLowerCase(c1);

					c2 = szyfrowanie(c1);

					//PokazWirniki();
					PokazOkno();
					logFp.writeByte(c1);
					PokazKroki();
					logFp.writeBytes("\r\n");
					linaWyjsciowa[i] = c2;
					outFp.writeByte(linaWyjsciowa[i]);
				}
				outFp.writeBytes("\r\n");
				
			}
		}
		//funkcaj szyfrujaca znak c 
		private static char szyfrowanie(char c)
		{
			int i, r, j;
			char[] rflector = new char [Mchars];

			obrot();                                          		//    obrot rotorow
			i = 0;                                           
			kroki[i] = lacznicaKablowa[indeks(c)];           		//    lacznica kablowa
			i++;
			for (r = 1; r <= liczbaWirnikow; ++r)
				{
					kroki[i] = RtoLpath(kroki[i - 1], r);     		//    sciezka z prawej do lewej 
					i++;
				}
			for(j = 0; j < reflektor.length(); j++)
				rflector[j] = reflektor.charAt(j);
			
			kroki[i] = rflector[indeks(kroki[i - 1])]; 				//    reflektor
			i++;
			for (r = liczbaWirnikow; r >= 1; --r)                 	//    sciezka z lewej do prawej 
				{
				kroki[i] = LtoRpath(kroki[i - 1], r);
				i++;
				}
			kroki[i] = lacznicaKablowa[indeks(kroki[i - 1])];   	//    lacznica kablowa

			return kroki[i];
		}
		
		private static int indeks(char c)
		{
			// pozycja znaku c w alfabecie

			int i = 0;

			while ((i < Nchars) && (c != ElementyEnigmy.alfabet.charAt(i)))
				++i;
			return i;
		}
		
		private static void obrot()   // okreslenie ktory rotor ma sie obrocic
		{
			int n;
			int[] doit = new int [Nrotors];
			String rr1 = uzwojeniaWirnika[1], rr2 = uzwojeniaWirnika[2], rr3;
			char[] r1 = new char[Nchars];
			char[] r2 = new char[Nchars];
			char[] r3 = new char[Nchars];
			if (liczbaWirnikow > 3)
			{
				rr3 = uzwojeniaWirnika[3];
				for(int j = 0; j < rr1.length(); j++)
					{
						r1[j] = rr1.charAt(j);
						r2[j] = rr2.charAt(j);
						r3[j] = rr3.charAt(j);
					}
				
			}
			for(int j = 0; j < rr1.length(); j++)
				{
					r1[j] = rr1.charAt(j);
					r2[j] = rr2.charAt(j);
				}
				
			// obliczenie ilości kroków dla poszczegolnych rotorow
			doit[1] = 1;
			for (int i = 2; i <= liczbaWirnikow; ++i)
				doit[i] = 0;
			if ((wciecieWirnika[1] == r1[pozWirnikow[1]])||(wciecieWirnika[2] == r2[pozWirnikow[2]]))  // podwojny krok 
				doit[2] = 1;
			if (wciecieWirnika[2] == r2[pozWirnikow[2]])
				doit[3] = 1;
			if (liczbaWirnikow > 3) {
				if (wciecieWirnika[3] == r3[pozWirnikow[3]])
					doit[4] = 1;
			}

			// rownoczesny obrot rotorow
			for (n = 1; n <= liczbaWirnikow; ++n)
				ObrotWirnikow(n, doit[n]);
		}
		
		private static void ObrotWirnikow(int n, int width)   // obrot wirnika "n" o width
		{                                 
			// jezeli dojdzie do ostatniego uzwojenia to obrot wirnika zaczyna sie od początku 
			String rr;
			char[] r = new char [Nchars];

			if (width > 0) 
			{
				pozWirnikow[n] = mod(pozWirnikow[n] + width, Nchars);
				rr = uzwojeniaWirnika[n];
				for(int j = 0; j < rr.length(); j++)
				{
					r[j] = rr.charAt(j);
				}
				okno[n] = r[pozWirnikow[n]];
			}
		}
		
		private static int mod(int n, int modulus)  // funkcja modulo 
		{
			while (n >= modulus)
				n -= modulus;
			while (n < 0)
				n += modulus;
			return n;
		}
		
		// Zamiana znaku w rotorze z prawej strony na lewa 
		private static char RtoLpath(char c, int r)  // zamiana znaku "c" w rotorze "r" 
		{
			int n, offset, idx, ret;
			String CurRotorString;
			char[] CurRotor = new char [Nchars];
			CurRotorString = uzwojeniaWirnika[r];
			for(int j = 0; j < CurRotorString.length(); j++)
			{
				CurRotor[j] = CurRotorString.charAt(j);
			}
			n = indeks(c);
			offset = indeks(CurRotor[pozWirnikow[r]]);
			idx = mod(n + offset, Nchars);
			ret = mod(indeks(CurRotor[idx]) - offset, Nchars);
			return ElementyEnigmy.alfabet.charAt(ret);
		}
		
		// Zamiana znaku w rotorze z lewej strony do prawej 
		private static char LtoRpath(char c, int r) // zamiana znaku "c" w rotorze "r" 
		{
			int n, m, offset, idx, newchar;
			String CurRotorString;
			char[] CurRotor = new char [Nchars];
			CurRotorString = uzwojeniaWirnika[r];
			for(int j = 0; j < CurRotorString.length(); j++)
			{
				CurRotor[j] = CurRotorString.charAt(j);
			}
			n = indeks(c);
			offset = indeks(CurRotor[pozWirnikow[r]]);
			newchar = ElementyEnigmy.alfabet.charAt(mod(n + offset, Nchars));

			m = 0;
			while (m < Nchars && CurRotor[m] != newchar)
				++m;
			idx = mod(m - offset, Nchars);
			return ElementyEnigmy.alfabet.charAt(idx);
		}
		//funkcja zapisujaca do pliku log pozycje pierscienia w oknie enigmy
		private static void PokazOkno() throws IOException
		{
			int i;

			for (i = liczbaWirnikow; i >= 1; --i)
				logFp.writeBytes(okno[i] + "  ");
			logFp.writeBytes("  ");
		}
		//funkcja zapisujaca do pliku log kroki szyfrowania
		private static void PokazKroki() throws IOException
		{
			int i;

			for (i = 0; i < liczbaKrokow; ++i)
				logFp.writeBytes(" " + "->" + " " + kroki[i]);
				
		}
		//funkcja zamykajaca pliki
		private static void ZamknijPliki() throws IOException 
		{
			inFp.close();
			outFp.close();
			outFp.close();
			logFp.close();
		}
	}
	
}
