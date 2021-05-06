	/* ICount.java
 * Sample program using BIT -- counts the number of instructions executed.
 *
 * Copyright (c) 1997, The Regents of the University of Colorado. All
 * Rights Reserved.
 * 
 * Permission to use and copy this software and its documentation for
 * NON-COMMERCIAL purposes and without fee is hereby granted provided
 * that this copyright notice appears in all copies. If you wish to use
 * or wish to have others use BIT for commercial purposes please contact,
 * Stephen V. O'Neil, Director, Office of Technology Transfer at the
 * University of Colorado at Boulder (303) 492-5647.
 */

import BIT.highBIT.*;
import java.io.*;
import java.util.*;


public class Metrics {

    private static PrintStream out = null;
    private static int i_count = 0, b_count = 0, m_count = 0;
    
    private static HashMap<Long, Integer> i_count_map = new HashMap<>();    //instructions count map
    private static HashMap<Long, Integer> m_count_map = new HashMap<>();    //methods count map
    private static HashMap<Long, Integer> b_count_map = new HashMap<>();    //basic blocks count map

    /* main reads in all the files class files present in the input directory,
     * instruments them, and outputs them to the specified output directory.
     */
    public static void main(String argv[]) {
        File file_in = new File(argv[0]);        
        String infilenames[] = file_in.list();

        for (int i = 0; i < infilenames.length; i++) {
            String infilename = infilenames[i];
            if (infilename.endsWith(".class")) {
				// create class info object
				ClassInfo ci = new ClassInfo(argv[0] + System.getProperty("file.separator") + infilename);

                // loop through all the routines
                // see java.util.Enumeration for more information on Enumeration class
                for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements(); ) {
                    Routine routine = (Routine) e.nextElement();
					routine.addBefore("Metrics", "mcount", new Integer(1));
                    

                    if (routine.getMethodName().equals("solveImage")) {
                        System.out.println("Added logToFile.");
                        routine.addAfter("Metrics", "logToFile", ci.getClassName());
                    }

                    for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements(); ) {
                        BasicBlock bb = (BasicBlock) b.nextElement();
                        bb.addBefore("Metrics", "count", new Integer(bb.size()));
                    }
                }
                //ci.addAfter("Metrics", "printMetrics", ci.getClassName());
                //addLogToFile(ci);
                ci.write(argv[1] + System.getProperty("file.separator") + infilename);
            }
        }
    }
        
    /*public static void addLogToFile(ClassInfo ci) {
        for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements(); ) {
            Routine routine = (Routine) e.nextElement();
            routine.addBefore("Metrics", "mcount", new Integer(1));
            
            for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements(); ) {
                BasicBlock bb = (BasicBlock) b.nextElement();
                bb.addBefore("Metrics", "count", new Integer(bb.size()));
            }
        }


    }*/


    
    public static synchronized void printMetrics(String foo) {
        System.out.println(i_count + " instructions in " + b_count + " basic blocks were executed in " + m_count + " methods.");
    }
    

    public static synchronized void count(int incr) {
        i_count += incr;
        b_count++;

        long thread_id = Thread.currentThread().getId();
        if(! i_count_map.containsKey(thread_id)) {
            i_count_map.put(thread_id, 0);
        }else {
            i_count_map.put(thread_id, i_count_map.get(thread_id)+incr);
        }

        if(! b_count_map.containsKey(thread_id)) {
            b_count_map.put(thread_id, 0);
        }else {
            b_count_map.put(thread_id, b_count_map.get(thread_id)+1);
        }

    }

    public static synchronized void mcount(int incr) {
        long thread_id = Thread.currentThread().getId();
        if(! m_count_map.containsKey(thread_id)) {
            m_count_map.put(thread_id, 0);
        }else {
            m_count_map.put(thread_id, m_count_map.get(thread_id)+1);
        }
    }

    public static synchronized void logToFile(String s) {
        try {
            long thread_id = Thread.currentThread().getId();
            File log = new File("log.txt");
            log.createNewFile();

            BufferedWriter w = new BufferedWriter(new FileWriter(log, true));

            w.write("Thread #" + Thread.currentThread().getId() + ":\n");
            w.write(">" + (i_count_map.containsKey(thread_id) ? i_count_map.get(thread_id) : -1) + " instructions\n");
            w.write(">" + (b_count_map.containsKey(thread_id) ? b_count_map.get(thread_id) : -1) + " basic blocks\n");
            w.write(">" + (m_count_map.containsKey(thread_id) ? m_count_map.get(thread_id) : -1) + " methods\n");
            w.close();

        } catch(IOException e) {
            System.out.println("Error while logging.");
        }
    }
}

