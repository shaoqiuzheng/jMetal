package org.uma.jmetal.operator.impl.localsearch;

import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import java.util.Comparator;


/**
 * This class implements an local search operator based in the use of a
 * mutation operator. An archive is used to store the non-dominated solutions
 * found during the search.
 */
public class MutationLocalSearch implements LocalSearchOperator<Solution>{


    /**
     * Stores the problem to solve
     */
    private Problem problem;

    /**
     * Stores a reference to the archive in which the non-dominated solutions are
     * inserted
     */
    private Archive archive;

    private int improvementRounds ;

    /**
     * Stores comparators for dealing with constraints and dominance checking,
     * respectively.
     */
    private Comparator constraintComparator ;
    private Comparator dominanceComparator ;
    /**
     * Stores the mutation operator
     */
    private MutationOperator mutationOperator;

    /**
     * Stores the number of evaluations_ carried out
     */
    private int evaluations ;

    /**
     * Constructor.
     * Creates a new local search object.
     * @param improvementRounds number of iterations
     * @param mutationOperator mutation operator
     * @param archive archive to store non-dominanted solution
     * @param problem problem to resolve

     */

    public  MutationLocalSearch(int improvementRounds,MutationOperator mutationOperator,
                                Archive archive,Problem problem ){
        this.problem=problem;
        this.mutationOperator=mutationOperator;
        this.improvementRounds=improvementRounds;
        this.archive=archive;
        dominanceComparator  = new DominanceComparator();
        constraintComparator = new OverallConstraintViolationComparator();
        evaluations=0;
    }


    /**
     * Executes the local search. The maximum number of iterations is given by
     * the param "improvementRounds", which is in the parameter list of the
     * operator. The archive to store the non-dominated solutions is also in the
     * parameter list.
     * @param  solution representing a solution
     * @return An object containing the new improved solution
     */
    public Solution  execute(Solution solution)  {
        int i = 0;
        int best = 0;
        evaluations = 0;

        int rounds = improvementRounds;

        if (rounds <= 0)
            return solution.copy();

        do
        {
            i++;
            Solution mutatedSolution = solution.copy();

            mutationOperator.execute(mutatedSolution);


            // Evaluate the getNumberOfConstraints
            if (problem.getNumberOfConstraints() > 0)
            {

                ((ConstrainedProblem)problem).evaluateConstraints(mutatedSolution);
                best = constraintComparator.compare(mutatedSolution,solution);
                if (best == 0) //none of then is better that the other one
                {
                    problem.evaluate(mutatedSolution);
                    evaluations++;
                    best = dominanceComparator.compare(mutatedSolution,solution);
                }
                else if (best == -1) //mutatedSolution is best
                {
                    problem.evaluate(mutatedSolution);
                    evaluations++;
                }
            }
            else
            {
                problem.evaluate(mutatedSolution);
                evaluations++;
                best = dominanceComparator.compare(mutatedSolution,solution);
            }
            if (best == -1) // This is: Mutated is best
                solution = mutatedSolution;
            else if (best == 1) // This is: Original is best
                //delete mutatedSolution
                ;
            else // This is mutatedSolution and original are non-dominated
            {

                if (archive!= null)
                    archive.add(mutatedSolution);
            }
        }
        while (i < rounds);
        return solution.copy();
    } // execute


    /**
     * Returns the number of evaluations
     */
    public int getEvaluations() {
        return evaluations;
    } // evaluations

}
