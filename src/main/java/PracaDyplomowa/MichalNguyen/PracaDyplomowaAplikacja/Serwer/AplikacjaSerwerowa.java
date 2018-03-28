package PracaDyplomowa.MichalNguyen.PracaDyplomowaAplikacja.Serwer;


import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import PracaDyplomowa.MichalNguyen.PracaDyplomowaAplikacja.Enigma.Enigma;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@Path("pracadyplomowa")
public class AplikacjaSerwerowa {

	//zdefiniownie lokalizacji odczytu i zapisu plikow 
	public static String localizacjaZapisuPlikuWyslanego = "E://PracaDyplomowa/PracaDyplomowaAplikacja/PlikiTymczasoweWyslane/";
	public static String localizacjaZapisuPlikuZaszyfrowanego = "E://PracaDyplomowa/PracaDyplomowaAplikacja/PlikiZaszyfrowane/";
	public static String logSzyfrowania = "E://PracaDyplomowa/PracaDyplomowaAplikacja/logSzyfrowania/elog";
	public static String logDeszyfrowania = "E://PracaDyplomowa/PracaDyplomowaAplikacja/logDeszyfracja/dlog";
	
	public static int licencja = 1;
	
	final Application application = new ResourceConfig()
		    .packages("org.glassfish.jersey.examples.multipart")
		    .register(MultiPartFeature.class);
	
	//Czesc serwisu odpowiadajaca za otrzymywanie plikow i ich szyfrowaniu
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response PrzesylaniePliku(
			@FormDataParam("file") InputStream uploadInputStream, 
			@FormDataParam("file") FormDataContentDisposition fileContent)
	{
		String nazwaPliku = fileContent.getFileName();
		String miejsceZapisuPliku = localizacjaZapisuPlikuWyslanego + nazwaPliku;
		//zapis pliku do folderu tymczasowego
		ZapisNaDysku(uploadInputStream, miejsceZapisuPliku);
		String miejsceZapisuPlikuZaszyfrowanego = localizacjaZapisuPlikuZaszyfrowanego +  nazwaPliku;
		//sprawdzenie licencji 
		if(licencja != 0){
			try {
				//szyfrowanie pliku wejsciwego i jego zapis w folderze pliki zaszyfrowane,
				//oraz usuniecie pliku z folderu plike tymczaswe 
				Enigma.startEnigma(miejsceZapisuPliku, miejsceZapisuPlikuZaszyfrowanego, logSzyfrowania, nazwaPliku);
				//System.out.print("Dziala");
				File plikWejsciowy = new File (miejsceZapisuPliku);
				plikWejsciowy.delete();
			} catch (IOException e) {
				e.printStackTrace();
				//System.out.print("Nie Dziala");
			}
		}
		else {
			try {
				//przenisienie pliku do folderu pliki zaszyfrowane,
				//usuniecie pliku z folderu pliki tymczasowe
				File plikWejsciowy = new File (miejsceZapisuPliku);
				File plikWyjściowy = new File (miejsceZapisuPlikuZaszyfrowanego);
				InputStream strumienWejsciowy = new FileInputStream(plikWejsciowy);
				OutputStream strumienWyjsciowy = new FileOutputStream(plikWyjściowy);
				byte [] buffor = new byte[1024];
				int lenght;
				while((lenght = strumienWejsciowy.read(buffor))>0)
				{
					strumienWyjsciowy.write(buffor, 0, lenght);
				}
				strumienWejsciowy.close();
				strumienWyjsciowy.close();
				plikWejsciowy.delete();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		//utworzenie odwolania do pliku w postaci URL 
		String output = "http://localhost:8080/PracaDyplomowaAplikacja/pracadyplomowa/download/" + nazwaPliku;
		//utworzenie odpowiedzi servera z statusem 
		return Response.status(200).entity(output).build();
	}
	
	//czesc servisu odpowiedzialna za wysylanie pliku i jego odszyfrowanie 
	@GET
	@Path("/download/{nazwaPliku}")
	@Produces("text/plain")
	public Response PobieraniePliku(@PathParam("nazwaPliku") String nazwaPliku)
	{
		File plikWejsciowy = null;
		String localizacjaPliku = localizacjaZapisuPlikuZaszyfrowanego + nazwaPliku;
		String localizacjaPlikuTymczasowego = "E://PracaDyplomowa/PracaDyplomowaAplikacja/PlikiTymczasoweOdszyfrowane/" + nazwaPliku;
		if(licencja != 0){
			try {
				Enigma.startEnigma(localizacjaPliku, localizacjaPlikuTymczasowego, logDeszyfrowania, nazwaPliku);
				plikWejsciowy = new File (localizacjaPlikuTymczasowego);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			plikWejsciowy = new File (localizacjaPliku);
			
			}
		
		
		ResponseBuilder response = Response.ok((Object) plikWejsciowy);
	    response.header("Content-Disposition", "attachment; filename=\"" + nazwaPliku + "");
	   
	    
	    return response.build();
	}

	//funkcja zapisujaca plik wejściowy na dysku 
	private void ZapisNaDysku (
		InputStream uploadInputStream, 
		String MiejsceZapisuPliku)
	{
		
		try{
			OutputStream out = new FileOutputStream(new File(MiejsceZapisuPliku));
			int czytaj = 0;
			byte[] bytes = new byte[2048];
			
			while((czytaj = uploadInputStream.read(bytes)) != -1)
			{
				out.write(bytes, 0, czytaj);
			}
			out.flush();
			out.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
	




