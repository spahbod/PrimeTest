package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import oracle.jdbc.pool.OracleDataSource;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;

@ManagedBean
public class Filemanagement {
    
    private String destination="D:\\test\\";
    private DefaultStreamedContent download;
    public static String url_dazy_danych = "jdbc:oracle:thin:@//localhost:1521/XE";
   // public static String zaloguj_do_bazy_danych_login = "log";
    public static String zaloguj_do_bazy_danych_main = "SZD";
    
    
    private OracleDataSource ods;

public void setDownload(DefaultStreamedContent download) {
    this.download = download;
}

public DefaultStreamedContent getDownload() throws Exception {
    System.out.println("GET = " + download.getName());
    return download;
}

public void prepDownload() throws Exception {
    File file = new File("D:\\test\\test4.jpg");
    
         try{   
      Connection conn = ods.getConnection();
       Statement stmt = conn.createStatement();  
                String selectStatement = "select * from SZD.test_b where id_test = 4";
                PreparedStatement prepStmt = conn.prepareStatement(selectStatement);;
                ResultSet rset=prepStmt.executeQuery();               
                int licznik = 0;
                 while (rset.next()){                       
                        licznik++;
                        Blob blob = rset.getBlob(2);                       
                        InputStream is = blob.getBinaryStream();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int r = 0;
                        while((r = is.read(buffer))!=-1) {
                              fos.write(buffer, 0, r);
                            }
                        is.close();
                        fos.flush();
                        fos.close();
                        blob.free();                              
                    }    
                 rset.close();
                 stmt.close();
                conn.close();
               System.out.print(licznik);
      
     }
     catch(Exception e){
         System.out.print(e.toString());       
     }          
    InputStream input = new FileInputStream(file);
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    setDownload(new DefaultStreamedContent(input, externalContext.getMimeType(file.getName()), file.getName()));
    System.out.println("PREP = " + download.getName());
    
}

   public void handleFileUpload(FileUploadEvent event) throws SQLException,IOException{  
        System.out.print("hello"+event.getFile().getFileName());
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
        
       /* try {
        copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
        } catch (IOException e) {

        e.printStackTrace()
       
}*/ 
     try{   
      Connection conn = ods.getConnection();
       Statement stmt = conn.createStatement();  
                String selectStatement = "insert into SZD.test_b(id_test,test) values (?,?)";
                PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
                prepStmt.setInt(1,6);
                //prepStmt.setNull(2, java.sql.Types.BLOB);
                prepStmt.setBinaryStream(2, event.getFile().getInputstream());
                prepStmt.executeQuery();
                stmt.close();
                conn.close();
      
     }
     catch(Exception e){
         System.out.print(e.toString());
         
     }
    } 
   
/*public void copyFile(String fileName, InputStream in) {
    try {
// write the inputStream to a FileOutputStream
        OutputStream out = new FileOutputStream(new File(destination + fileName));
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = in.read(bytes)) != -1) {
               out.write(bytes, 0, read);
        }
      in.close();
        out.flush();
        out.close();
     System.out.println("New file created!");
    } catch (IOException e) {
    System.out.println(e.getMessage());
    }
}*/
    public Filemanagement() {
         try{
         ods = new OracleDataSource();
         ods.setURL(Filemanagement.url_dazy_danych);
         ods.setUser(Filemanagement.zaloguj_do_bazy_danych_main);
         ods.setPassword(Filemanagement.zaloguj_do_bazy_danych_main); 
         Connection conn = ods.getConnection(); 
         
       }
       catch(Exception e){
           System.out.print(e.toString());       
       }
        
    }
}
