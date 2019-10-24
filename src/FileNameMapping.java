import java.io.File;

public class FileNameMapping {
	public void mapFileName ( String sourceFolderName, String targetFolderName, String newSourceFolderName ) {
		File sourceFolder = new File ( sourceFolderName );
		String[] sourceFileList = sourceFolder.list();
		
		File targetFolder = new File ( targetFolderName );
		String[] targetFileList = targetFolder.list();
		
		for ( int i =0; i < sourceFileList.length; i++ ) {
			String sourceFileName = sourceFileList[i];
			String[] temp = sourceFileName.split( "-");
			String str = temp[2].substring( 0, temp[2].length() - new String(".csv").length() );
			int index = Integer.parseInt( str);
			
			int prefixIndex = sourceFileName.indexOf( ".csv" );
			String namePrefix = sourceFileName.substring( 0, prefixIndex );
			
			String nameDetails = "";
			for ( int j =0; j < targetFileList.length; j++ ) {
				String targetFileName = targetFileList[j];
				String[] temp2 = targetFileName.split( "-");
				int index2 = Integer.parseInt( temp2[0]);
				if ( index == index2 ) {
					nameDetails = targetFileName.substring( temp2[0].length()+1 );
				}
			}
			
			String newName = namePrefix + "-" + nameDetails;
			System.out.println( namePrefix + " " + nameDetails + " " + newName );
			
			File sourceFile = new File ( sourceFolderName + "/" + sourceFileName );
			sourceFile.renameTo( new File ( newSourceFolderName + "/" + newName ));
		}
	}
	
	public static void main ( String[] args ) {
		FileNameMapping mapping = new FileNameMapping();
		for ( int i = 1; i <=15; i++ ) {
			String sourceFolderName = "data/output/performance/ARIMA-raw/ARIMA-" + i ;
			String targetFolderName = "data/input/projects";
			String newSourceFolderName = "data/output/performance/ARIMA-raw/ARIMA-" + i ;
			mapping.mapFileName(sourceFolderName, targetFolderName, newSourceFolderName);
		}
		
	}
}
