package edu.neu.genetic.algorithm;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Population {

    private List<City> baseRoute;
    private int populationSize = GeneticAlgorithm.POPULATION_SIZE;
    private List<TSPGenome> genomeList;
    private double cutoff;

    public Comparator<TSPGenome> genoTypeComparator
            = new Comparator<TSPGenome>() {

        public int compare(TSPGenome g1, TSPGenome g2) {
            return g2.compareTo(g1);
        }
    };

    public Population(int populationSize, List<City> cities, String[] geneString, List<City> baseRoute) {
        this.genomeList = new ArrayList<>();
        this.cutoff = cutoff;
        this.baseRoute = baseRoute;
    }

    public List<TSPGenome> getGenomeList() {
        return genomeList;
    }

    public void setGenomeList(List<TSPGenome> genomeList) {
        this.genomeList = genomeList;
    }

//    public void sortRoutesByFitness() {
//        routes.sort((route1, route2) -> {
//            int flag = 0;
//            if (route1.getFitness() > route2.getFitness()) flag = -1;
//            else if (route1.getFitness() < route2.getFitness()) flag = 1;
//            return flag;
//        });
//    }

    public void initPopulation(int genolength, int phenolength) {
        Random r = new Random();

        //generate genome objects equivalent to size of population
        this.genomeList = IntStream.range(0, populationSize)
                // String rep is twice length of genome.
                .mapToObj(g -> new TSPGenome(genolength * 2, g))
                .collect(Collectors.toList());


        this.genomeList.stream().forEach(tspGenome -> {
            IntStream.range(0, genolength * 2).forEach(i -> {
                int randInt = r.nextInt(phenolength);
                //creates gene string represention and links to the genome
                tspGenome.getGenString()[i] = String.valueOf(randInt);
            });
        });

        //link phenome with genome by adding new route created
        this.genomeList.stream().forEach(tspGenome -> {
            List<City> newBaseRoute = new ArrayList<>();
            baseRoute.stream().forEach(city -> newBaseRoute.add(city));
            tspGenome.generatePhenome(newBaseRoute);
        });
    }

    public void sortPopulation() {
        Collections.sort(this.genomeList, this.genoTypeComparator);
    }

    public void regeneration() {
        Random r = new Random();
        int ubound = (int) ((1 - this.cutoff) * this.genomeList.size());

        List<TSPGenome> newGeneration = IntStream.range(0, this.genomeList.size())
                .mapToObj(index -> {

                    int firstParent = r.nextInt((ubound));
                    int secondParent = r.nextInt((ubound));

                    while (firstParent == secondParent) {
                        secondParent = r.nextInt((ubound));
                    }
                    TSPGenome child = crossover(firstParent, secondParent, index);
                    return child;
                }).collect(Collectors.toList());

        this.genomeList = newGeneration;
    }

    public TSPGenome crossover(int firstParent, int secondParent, int newMemberId) {
        TSPGenome firstGenome = this.genomeList.get(firstParent);
        TSPGenome secondGenome = this.genomeList.get(secondParent);
        String[] childGenString = new String[firstGenome.getGenString().length];
        IntStream.range(0, childGenString.length)
                .forEach(index -> {
                    if (index < childGenString.length / 2) {
                        childGenString[index] = firstGenome.getGenString()[index];
                    } else {
                        childGenString[index] = secondGenome.getGenString()[index];
                    }
                });

        TSPGenome child = new TSPGenome(newMemberId);
        child.setGenString(childGenString);
        child.generatePhenome(this.baseRoute);
        return child;
    }
}
