/*
 * This file is part of the PSL software.
 * Copyright 2011 University of Maryland
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
package edu.umd.cs.psl.model.kernel;

import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import edu.umd.cs.psl.factorgraph.Factor;
import edu.umd.cs.psl.factorgraph.RandomVariable;
import edu.umd.cs.psl.model.atom.Atom;

/**
 * 
 * Evidence is responsible for maintenance of the numeric representation, as
 * well as registration with any affected atoms. 
 * 
 * @author matthias
 * 
 */
public abstract class GroundKernel implements Factor {

	/**
	 * This method is called by
	 * {@link edu.umd.cs.psl.application.ModelApplication ModelApplication}
	 * after it has been alerted to changes in the parameters of an
	 * {@link Kernel} on all {@link GroundKernel} instantiations of that type.
	 * 
	 * The method serves two purposes: 1) It allows the ground kernels to update
	 * their internal parameters if they are cached (for instance, because they
	 * require non-trivial computation) 2) It allows the
	 * {@link edu.umd.cs.psl.application.ModelApplication ModelApplication} to
	 * determine whether the change in the parameters of the kernel effects this
	 * particular instantiation. If the method returns TRUE, then it does,
	 * otherwise the ground kernel is unaffected. Note that any call to this
	 * method implicitly assumes that the kernel of this particular ground
	 * kernel has changed its parameters. Hence, this method might always return
	 * true or throw an {@link UnsupportedOperationException} if the parameters
	 * could not possibly have changed.
	 * 
	 * @return Whether or not the change in parameters of the associated Kernel
	 *         affected this particular instantiation.
	 * @throws UnsupportedOperationException
	 *             If there are no parameters to change.
	 */
	abstract public boolean updateParameters();

	abstract public Kernel getKernel();

	abstract public Set<Atom> getAtoms();

	abstract public double getIncompatibility();

	abstract public BindingMode getBinding(Atom atom);

	@Override
	public Set<? extends RandomVariable> getRandomVariables() {
		return Sets.filter(getAtoms(), new Predicate<Atom>() {

			@Override
			public boolean apply(Atom atom) {
				return atom.isInferenceAtom();
			}

		});
	}

}
