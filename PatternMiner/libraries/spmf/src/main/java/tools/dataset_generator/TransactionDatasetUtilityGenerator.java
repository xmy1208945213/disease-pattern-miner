package tools.dataset_generator;
/* This file is copyright (c) 2008-2012 Philippe Fournier-Viger
 *
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 *
 * SPMF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.*;
import java.util.*;

/**
 * Convert a transaction database to a transaction database with utility values
 * from the source code.
 *
 * @author Philippe Fournier-Viger, 2010
 */
public class TransactionDatasetUtilityGenerator {

    /**
     * Convert a transaction database to a transaction database with utility values
     * from the source code.
     *
     * @param input                 the input file path (a transaction database in SPMF format)
     * @param output                the output file path
     * @param maxQuantity           the maximum quantity of each item in a transaction
     * @param externalUtilityFactor the external utility of items generated by Random.nextGaussian() will be multiplied by this value
     * @throws IOException           if an error while reading/writting files.
     * @throws NumberFormatException
     */
    public void convert(String input, String output, int maxQuantity, double externalUtilityFactor) throws NumberFormatException, IOException {

        // for stats
        Set<Integer> items = new HashSet<Integer>();
        long avglength = 0;
        long tidcount = 0;

        Random randomGenerator = new Random(System.currentTimeMillis());

        Map<Integer, Integer> externalUtilities = new HashMap<Integer, Integer>();

        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        BufferedReader myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
        // for each line (transaction) until the end of file
        String thisLine;
        while ((thisLine = myInput.readLine()) != null) {
            // if the line is  a comment, is  empty or is a
            // kind of metadata
            if (thisLine.isEmpty() == true ||
                    thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                    || thisLine.charAt(0) == '@') {
                continue;
            }

            // split the transaction according to the : separator
            String split[] = thisLine.split(" ");


            tidcount++;
            avglength += split.length;

            for (int i = 0; i < split.length; i++) {
                // convert item to integer
                Integer item = Integer.parseInt(split[i]);

                items.add(item);

                if (externalUtilities.containsKey(item) == false) {
                    double rand = Math.abs(randomGenerator.nextGaussian() * externalUtilityFactor);
//					System.out.println("rand " + rand);
                    int extUtility = (int) (rand) + 1;
                    externalUtilities.put(item, extUtility);
//					System.out.println(extUtility);
                }
            }
        }
        myInput.close();

        myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
        // for each line (transaction) until the end of file
        while ((thisLine = myInput.readLine()) != null) {
            // if the line is  a comment, is  empty or is a
            // kind of metadata
            if (thisLine.isEmpty() == true ||
                    thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                    || thisLine.charAt(0) == '@') {
                continue;
            }

            // split the transaction according to the : separator
            String split[] = thisLine.split(" ");

            List<Integer> quantities = new ArrayList<Integer>();
            int TU = 0;

            // split the transaction according to the : separator
            for (int i = 0; i < split.length; i++) {
                // convert item to integer
                Integer item = Integer.parseInt(split[i]);
                int quantity = randomGenerator.nextInt(maxQuantity) + 1;
                quantities.add(quantity);
                int extutility = externalUtilities.get(item);
                TU += extutility * quantity;
            }

            for (int i = 0; i < split.length; i++) {
                // convert item to integer
                Integer item = Integer.parseInt(split[i]);
                writer.write("" + item);
                if (i != split.length - 1) {
                    writer.write(" ");
                }
            }
            writer.write(":");
            writer.write("" + TU);
            writer.write(":");
            for (int i = 0; i < split.length; i++) {
                // convert item to integer
                Integer item = Integer.parseInt(split[i]);
                Integer q = quantities.get(i);
                int extutility = externalUtilities.get(item);
                writer.write("" + q * extutility);
                if (i != split.length - 1) {
                    writer.write(" ");
                }
            }
            writer.newLine();
        }
        writer.close();

        System.out.println("item count " + items.size());
        System.out.println("transaction count " + tidcount);
        System.out.println("transaction avg length " + (avglength / (double) tidcount));
    }

}

	