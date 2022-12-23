/*
 * Copyright 2017 Courtanet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.doov.sample.validation;

import static io.doov.core.dsl.time.LocalDateSuppliers.today;
import static io.doov.sample.field.dsl.DslSampleModel.*;

import io.doov.core.dsl.DOOV;
import io.doov.core.dsl.lang.ValidationRule;
import io.doov.sample.model.Country;

public class RulesSoftshake {

    public static final ValidationRule EXAMPLE = DOOV.when(
            userBirthdate.ageAt(today()).greaterOrEquals(18)
                    .and(accountEmail.length().lesserThan(configurationMaxEmailSize))
                    .and(accountCountry.eq(Country.FR))
                    .and(accountPhoneNumber.startsWith("+33")))
            .validate();

}