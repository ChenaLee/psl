/*
 * This file is part of the PSL software.
 * Copyright 2011-2015 University of Maryland
 * Copyright 2013-2017 The Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.linqs.psl.application.learning.weight.maxlikelihood;

import org.linqs.psl.application.learning.weight.VotedPerceptron;
import org.linqs.psl.config.ConfigBundle;
import org.linqs.psl.database.Database;
import org.linqs.psl.model.rule.GroundRule;
import org.linqs.psl.model.rule.Rule;
import org.linqs.psl.model.rule.WeightedGroundRule;

import java.util.Arrays;
import java.util.List;

/**
 * Learns weights by optimizing the log likelihood of the data using
 * the voted perceptron algorithm.
 * <p>
 * The expected total incompatibility is estimated with the total incompatibility
 * in the MPE state.
 *
 * @author Stephen Bach <bach@cs.umd.edu>
 */
public class MaxLikelihoodMPE extends VotedPerceptron {
	private double[] fullObservedIncompatibility;
	private double[] fullExpectedIncompatibility;

	public MaxLikelihoodMPE(List<Rule> rules, Database rvDB, Database observedDB, ConfigBundle config) {
		super(rules, rvDB, observedDB, false, config);
	}

	@Override
	protected double[] computeExpectedIncomp() {
		fullExpectedIncompatibility = new double[mutableRules.size()];

		// Computes the MPE state.
		reasoner.optimize(termStore);

		// Computes incompatibility.
		for (int i = 0; i < mutableRules.size(); i++) {
			for (GroundRule groundRule : groundRuleStore.getGroundRules(mutableRules.get(i))) {
				fullExpectedIncompatibility[i] += ((WeightedGroundRule) groundRule).getIncompatibility();
			}
		}

		return Arrays.copyOf(fullExpectedIncompatibility, mutableRules.size());
	}

	@Override
	protected double[] computeObservedIncomp() {
		numGroundings = new double[mutableRules.size()];
		fullObservedIncompatibility = new double[mutableRules.size()];
		setLabeledRandomVariables();

		// Computes the observed incompatibilities and numbers of groundings.
		for (int i = 0; i < mutableRules.size(); i++) {
			for (GroundRule groundRule : groundRuleStore.getGroundRules(mutableRules.get(i))) {
				fullObservedIncompatibility[i] += ((WeightedGroundRule) groundRule).getIncompatibility();
				numGroundings[i]++;
			}
		}

		return Arrays.copyOf(fullObservedIncompatibility, mutableRules.size());
	}

	@Override
	protected double computeLoss() {
		double loss = 0.0;
		for (int i = 0; i < mutableRules.size(); i++) {
			loss += mutableRules.get(i).getWeight() * (fullObservedIncompatibility[i] - fullExpectedIncompatibility[i]);
		}

		return loss;
	}
}
