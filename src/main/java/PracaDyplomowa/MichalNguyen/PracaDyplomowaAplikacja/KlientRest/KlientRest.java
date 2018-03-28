package PracaDyplomowa.MichalNguyen.PracaDyplomowaAplikacja.KlientRest;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;


import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import javax.swing.JLabel;

public class KlientRest {
	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField txtServerStatusCode;
	private JTextField textField_4;

	//uruchomienie aplikacji
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KlientRest window = new KlientRest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//utworzenie aplikacji
	public KlientRest() {
		initialize();
	}

	//inicjalizacja zawartosci okienka
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setTitle("Aplikacja do wysyłania i pobierania plikow");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		//przeslanie pliku na server 
		JButton btnNewButton = new JButton("Wyślij");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent u) {
				//zdefiniwonie nowego klienta 
				final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
				File Plik = new File (textField.getText());
				String postawowyURL = "http://localhost:8080/PracaDyplomowaAplikacja/pracadyplomowa/upload";
				WebTarget serverTarget = client.target(postawowyURL);
				
				FileDataBodyPart filePart = new FileDataBodyPart("file", Plik);
				@SuppressWarnings("resource")
				MultiPart multipartEntity = new FormDataMultiPart().bodyPart(filePart);
				//zdefiniowanie odpowiezdzi z servera
				//zdefiniowanie rodzaju metody jaka chcemy wyegzekwowac od servera 
				Response response = serverTarget.request().post(Entity.entity(multipartEntity, multipartEntity.getMediaType()));
				//wyswietlenie statusu servera w textField_4
				textField_4.setText(" "+response.getStatus());
				//wyswietlenie linku do wyslanego pliku
				textField_2.setText(response.readEntity(String.class));
				response.close();
			}
		});
		btnNewButton.setBounds(335, 46, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		//wybor pliku z komputera po przez otwarcie okna dialogowego i wskazanie do niego sciezki dostepu
		JButton btnOtwrz = new JButton("Otwórz");
		btnOtwrz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser wPlik = new JFileChooser();
				wPlik.setCurrentDirectory(new File("C:\\Users\\xdmic\\Desktop"));
				wPlik.setDialogTitle("Wybierz Pilk Tekstowy");
				wPlik.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				wPlik.showOpenDialog(null);
				String lPlik = wPlik.getSelectedFile().getAbsolutePath();
				//przekazanie sciezki pliku do pola tekstowego textField
				textField.setText(lPlik);
			}
		});
		btnOtwrz.setBounds(236, 46, 89, 23);
		frame.getContentPane().add(btnOtwrz);
		
		//przycisk umożliwiajacy pobranie pliku znajdujacego się na serwerze
		JButton btnWylij = new JButton("Wyślij");
		btnWylij.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent d) {
				
				//zdefiniowanie nowego clienta
				final Client client = ClientBuilder.newClient(); 
				//zdefiniwonie scieżki dostępu na serwerze
				String URL = textField_1.getText();
				int dURL = URL.length();
				//wyodrebnienie nazwy pliku
				String nazwaPliku = URL.substring(URL.lastIndexOf("/") + 1, dURL);
				WebTarget serverTarget = client.target(URL);
				//zdefiniowanie odpowiezdzi z servera
				//zdefiniowanie rodzaju metody jaka chcemy wyegzekwowac od servera 
				Response response = serverTarget.request("text/plain").get();
				//zapisanie pliku na komputerze uzytkownika
				if(response.getStatus() == Response.Status.OK.getStatusCode())
				{
					InputStream streamPlik = response.readEntity(InputStream.class);
					zapisNaDysku(streamPlik, nazwaPliku);
				}
				//wyswietlenie statusu servera
				textField_4.setText(" "+response.getStatus());
				response.close();
				
			}
		});
		btnWylij.setBounds(335, 147, 89, 23);
		frame.getContentPane().add(btnWylij);
		
		//pole tekstowe w ktorym mozemy wpisac lub wkleic sciezke dostępu do pliku
		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String lPlik = textField.getText();
				System.out.print(lPlik);
			}
		});
		textField.setBounds(10, 47, 218, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		//pole tekstowe w ktorym wklejmy lub wpisujemy link do pliku zaszyfrowanego
		textField_1 = new JTextField();
		textField_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String link = textField.getText();
				System.out.print(link);
			}
		});
		textField_1.setBounds(10, 148, 218, 20);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		//pole tekstowe z linkiem do pliku zaszyfrowanego 
		textField_2 = new JTextField();
		textField_2.setBounds(10, 97, 315, 20);
		frame.getContentPane().add(textField_2);
		textField_2.setEditable(false);
		textField_2.setColumns(10);
		
		//tekst Dodaj plik
		JLabel lblNewLabel_2 = new JLabel("Dodaj Plik");
		lblNewLabel_2.setBounds(10, 26, 83, 14);
		frame.getContentPane().add(lblNewLabel_2);
		
		//tekst Twoj link do pliku
		JLabel lblNewLabel_3 = new JLabel("Twoj link do pliku: ");
		lblNewLabel_3.setBounds(10, 78, 130, 14);
		frame.getContentPane().add(lblNewLabel_3);
		
		//tekst Dodaj Link
		JLabel lblNewLabel_4 = new JLabel("Dodaj Link");
		lblNewLabel_4.setBounds(10, 128, 83, 14);
		frame.getContentPane().add(lblNewLabel_4);
		
		//przycik kopiujacy teks z textField_2 do schowka
		JButton btnKopiuj = new JButton("Kopiuj");
		btnKopiuj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		        Clipboard schowek = defaultToolkit.getSystemClipboard();
				schowek.setContents(new StringSelection(textField_2.getText()), null);
			}
		});
		btnKopiuj.setBounds(335, 96, 89, 23);
		frame.getContentPane().add(btnKopiuj);
		
		//pole tekstowe zawierajace tekst Server Status Code:
		txtServerStatusCode = new JTextField();
		txtServerStatusCode.setText("Server Status Code:");
		txtServerStatusCode.setEditable(false);
		txtServerStatusCode.setBounds(0, 241, 121, 20);
		frame.getContentPane().add(txtServerStatusCode);
		txtServerStatusCode.setColumns(10);
		
		//pole tekstowe zawierające status servera po otrzymaniu odpowiedzi
		textField_4 = new JTextField();
		textField_4.setEditable(false);
		textField_4.setBounds(119, 241, 315, 20);
		frame.getContentPane().add(textField_4);
		textField_4.setColumns(10);
		
	}
	//funkcja zapisujaca plik wejsciowy z Input stream na dysku komputera z możliwością zmiany lokalizacji pliku
	private void zapisNaDysku(InputStream strumien, String nazwaPlik) {
		//JFIleChooser otwiera okno dialogowe umożliwaijace zapis pliku na komputerze 
		JFileChooser nPlik = new JFileChooser();
		nPlik.setCurrentDirectory(new File("C:\\Users\\xdmic\\Desktop\\"));
		nPlik.setSelectedFile(new File(nazwaPlik));
		nPlik.setDialogTitle("Wybierz Pilk Tekstowy");
		if(nPlik.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
		{
			File zPlik = nPlik.getSelectedFile();
			try { 
				//skopiowanie zawartości Inputstream do pliku o podanej nazwie 
			    Files.copy(strumien, zPlik.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e){
				e.printStackTrace();
				
			}
		}
		
	}

}
