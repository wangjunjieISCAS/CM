
public class Test {
	public static void main ( String[] args ) {
		String type = "MhJK";
		String type2 = "MhJK-D";
		String str = "set term postscript eps enhanced color 20\r\n" + 
				"set termoption dash\r\n" + 
				"set output \"" + type + ".eps\"\r\n" + 
				"set datafile separator \",\"\r\n" + 
				"set title \"\"\r\n" + 
				"set xlabel \"Percentage of received reports\" \r\n" + 
				"set ylabel \"Relative error (>0:overestimate; <0:underestimate)\"\r\n" + 
				"set xrange [0.08:1.02]\r\n" + 
				"set yrange [-1.2:1.2]\r\n" + 
				"set linetype 2 dt 2\r\n" + 
				"plot '" + type + ".csv' using 1:2 with points pointtype 13 lc rgb \"gray50\" ps 1.4 title \"\", '" + type2 + ".csv' using 1:2 with points pointtype 11 ps 2.0 lc rgb \"blue\" title \"\", '" + type2 + ".csv' using 1:2 with lines lt 2 lw 3 lc rgb \"blue\" title \"\","
						+ "'"  + type2 + ".csv' using 7:8 with lines lw 2 lc rgb \"orange\" title \"\", '" + type2 + ".csv' " + 
				"using 3:4 with points pointtype 11 ps 2.0 lc rgb \"red\" title \"\",'" + type2 + ".csv' using 3:4 with lines lw 2 lc rgb \"red\" title \"\", '" + type2 + ".csv' using 5:6 with " + 
				"lines lt 2 lw 3 lc rgb \"green\" title \"\",'" + type2 + ".csv' using 5:6 with points pointtype 11 ps 2.0 lc rgb \"green\" title \"\"\r\n" + 
				"\r\n";
		
		System.out.println ( str ) ;
	}
}	
