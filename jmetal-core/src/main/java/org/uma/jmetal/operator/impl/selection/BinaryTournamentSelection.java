//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.operator.impl.selection;

import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.comparator.impl.DominanceComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 * @version 1.0
 * 
 * Applies a binary tournament selection to return the best solution between two that have been
 * chosen at random from a solution list.
 */
public class BinaryTournamentSelection implements SelectionOperator<List<Solution>,Solution> {
  private Comparator<Solution> comparator;
  private JMetalRandom randomGenerator ;

  /** Constructor */
  public BinaryTournamentSelection() {
    this(new DominanceComparator()) ;
  }

  /** Constructor */
  public BinaryTournamentSelection(Comparator<Solution> comparator) {
    randomGenerator = JMetalRandom.getInstance() ;
    this.comparator = comparator ;
  }

  @Override
  /** Execute() method */
  public Solution execute(List<Solution> solutions) {
    if (null == solutions) {
      throw new JMetalException("Parameter is null") ;
    } else if (solutions.isEmpty()) {
      throw new JMetalException("Solution set size is 0") ;
    }

    Solution result ;
    if (solutions.size() == 1) {
      result = solutions.get(0) ;
    } else {
      List<Solution> selectedSolutions = selectRandomSolutions(solutions) ;
      result = SolutionUtils.getBestSolution(selectedSolutions.get(0), selectedSolutions.get(1),comparator) ;
    }

    return result;
  }

  /**
   * Given a list with two or more solutions, selects two of them randomly
   * @param solutions The list of solutions
   * @return A list with two solutions
   */
  private List<Solution> selectRandomSolutions(List<Solution> solutions) {
    int indexSolution1 = randomGenerator.nextInt(0, solutions.size() - 1);
    int indexSolution2 = randomGenerator.nextInt(0, solutions.size() - 1);

    while (indexSolution1 == indexSolution2) {
      indexSolution2 = randomGenerator.nextInt(0, solutions.size() - 1);
    }

    return new ArrayList<>(Arrays.asList(solutions.get(indexSolution1), solutions.get(indexSolution2))) ;
  }
}