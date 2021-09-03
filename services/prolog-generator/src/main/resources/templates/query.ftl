:- include('facts').
:- include('helpers').

<#-- -------------------------------- -->
<#-- DISTRIBUTION WITHOUT CONSTRAINTS -->
<#-- -------------------------------- -->

<#list ast.distributions as distribution>
get_distribution_${distribution.name}(${distribution.name}) :-
    get_available_devices(AllAvailableDevices),

    % apply device properties that have to be matched

    % all devices should be online
    include(property("state","online"),AllAvailableDevices,AllAvailableDevicesFiltered1),

    % The following output lists have to be conjuncted
    <#assign count=1>
    <#assign count_conjunction=1>
    <#list distribution.selectionConjunctionProperties as selection>
        <#if selection.number == "1">
    include(property("${selection.key}","${selection.value}"),AllAvailableDevicesFiltered${count},ConjunctionOutput${count_conjunction}),
        <#elseif selection.number == "0">
    exclude(property("${selection.key}","${selection.value}"),AllAvailableDevicesFiltered${count},ConjunctionOutput${count_conjunction}),
        </#if>
        <#assign count_conjunction++>
    </#list>

    <#if distribution.selectionDisjunctionProperties?size gt 0>
    selection_disjunction([<#list distribution.selectionDisjunctionProperties as selection>["${selection.key}","${selection.value}",${selection.number}]<#sep>,</#sep></#list>], AllAvailableDevicesFiltered${count},ConjunctionOutput${count_conjunction}),
        <#assign count_conjunction++>
    </#if>

    % Finally apply conjunction
    <#if count_conjunction gt 1>
    apply_conjunction([<#list 1..count_conjunction-1 as i>ConjunctionOutput${i}<#sep>,</#sep></#list>],AllAvailableDevicesFiltered${count+1}),
    <#assign count++>
    </#if>

    % allow for reduction of deployed instances (e.g. for incompatibilities)
    sublist_max(AllAvailableDevicesFiltered${count+1},AllAvailableDevicesFiltered${count}),
    <#assign count++>

    % apply distribution constraints
    % first constrains equal: ==
    <#list distribution.equalConstraints as constraint>
    include_equal(property("${constraint.key}", "${constraint.value}"), ${constraint.number}, AllAvailableDevicesFiltered${count},AllAvailableDevicesFiltered${count+1}),
        <#assign count++>
    </#list>

    % then constrains less than equal: =<
    <#list distribution.lteConstraints as constraint>
    include_lte(property("${constraint.key}", "${constraint.value}"), ${constraint.number}, AllAvailableDevicesFiltered${count},AllAvailableDevicesFiltered${count+1}),
        <#assign count++>
    </#list>

    % then constrains greater than equal: >=
    <#list distribution.gteConstraints as constraint>
    check_gte(property("${constraint.key}", "${constraint.value}"), ${constraint.number}, AllAvailableDevicesFiltered${count}),
    </#list>

    % then constrains that check all equal
    <#list distribution.checkAllConstraints as constraint>
    check_include_all(property("${constraint.key}", "${constraint.value}"), AllAvailableDevicesFiltered, AllAvailableDevicesFiltered${count}),
    </#list>

    % bind result to target variable
    AllAvailableDevicesFiltered${count} = ${distribution.name}.

</#list>

<#-- -------------------------------- -->
<#--  DISTRIBUTION WITH CONSTRAINTS   -->
<#-- -------------------------------- -->
<#assign total_constraints = 0>

<#list ast.distributions as distribution>
<#assign total_constraints_this_distribution = distribution.equalConstraints?size + distribution.gteConstraints?size>
<#assign total_constraints += total_constraints_this_distribution>
get_distribution_allow_drop_${distribution.name}(${distribution.name}<#if total_constraints_this_distribution gt 0><#list 1..total_constraints_this_distribution as i>,Constraint${i}</#list></#if>) :-
    get_available_devices(AllAvailableDevices),

    <#assign count_constraint=1>
    % apply device properties that have to be matched

    % all devices should be online
    include(property("state","online"),AllAvailableDevices,AllAvailableDevicesFiltered1),

    % The following output lists have to be conjuncted
    <#assign count=1>
    <#assign count_conjunction=1>
    <#list distribution.selectionConjunctionProperties as selection>
        <#if selection.number == "1">
    include(property("${selection.key}","${selection.value}"),AllAvailableDevicesFiltered${count},ConjunctionOutput${count_conjunction}),
        <#elseif selection.number == "0">
    exclude(property("${selection.key}","${selection.value}"),AllAvailableDevicesFiltered${count},ConjunctionOutput${count_conjunction}),
        </#if>
        <#assign count_conjunction++>
    </#list>

    <#if distribution.selectionDisjunctionProperties?size gt 0>
    selection_disjunction([<#list distribution.selectionDisjunctionProperties as selection>["${selection.key}","${selection.value}",${selection.number}]<#sep>,</#sep></#list>], AllAvailableDevicesFiltered${count},ConjunctionOutput${count_conjunction}),
        <#assign count_conjunction++>
    </#if>

    % Finally apply conjunction
    <#if count_conjunction gt 1>
    apply_conjunction([<#list 1..count_conjunction-1 as i>ConjunctionOutput${i}<#sep>,</#sep></#list>],AllAvailableDevicesFiltered${count+1}),
    <#assign count++>
    </#if>

    % allow for reduction of deployed instances (e.g. for incompatibilities)
    sublist_max(AllAvailableDevicesFiltered${count+1},AllAvailableDevicesFiltered${count}),
    <#assign count++>

    % apply distribution constraints
    % first constrains equal: ==
    <#list distribution.equalConstraints as constraint>
        (
        include_equal(property("${constraint.key}", "${constraint.value}"), ${constraint.number}, AllAvailableDevicesFiltered${count},AllAvailableDevicesFiltered${count+1}), Constraint${count_constraint} = '';
        (\+include_equal(property("${constraint.key}", "${constraint.value}"), ${constraint.number}, AllAvailableDevicesFiltered${count} , _), Constraint${count_constraint} = '[EQ] ${distribution.name} ${constraint.key} ${constraint.value} == ${constraint.number}')
        ),
        <#assign count++>
        <#assign count_constraint++>
    </#list>

    <#assign count_constraint_backtrack=count_constraint>
    % look for a set of devices that can satisfy every constraint
    (
        % constrains less than equal: =<
    (
    <#list distribution.lteConstraints as constraint>
    include_lte(property("${constraint.key}", "${constraint.value}"), ${constraint.number}, AllAvailableDevicesFiltered${count},AllAvailableDevicesFiltered${count+1}),
        <#assign count++>
    </#list>

        % then constrains greater than equal: >=
    <#list distribution.gteConstraints as constraint>
        (
        check_gte(property("${constraint.key}", "${constraint.value}"), ${constraint.number}, AllAvailableDevicesFiltered${count}), Constraint${count_constraint} = ''
        ),
        <#assign count_constraint++>
    </#list>

    <#list distribution.checkAllConstraints as constraint>
    check_include_all(property("${constraint.key}", "${constraint.value}"), AllAvailableDevicesFiltered, AllAvailableDevicesFiltered${count}),
    </#list>

    true) ; (
    % if there is no set of devices that can satisfy every constraint, we'll fallback to the original behavior of dropping constraints
    <#assign count_constraint=count_constraint_backtrack>

    <#list distribution.lteConstraints as constraint>
    include_lte(property("${constraint.key}", "${constraint.value}"), ${constraint.number}, AllAvailableDevicesFiltered${count},AllAvailableDevicesFiltered${count+1}),
        <#assign count++>
    </#list>

    % then constrains greater than equal: >=
    <#list distribution.gteConstraints as constraint>
        (
        check_gte(property("${constraint.key}", "${constraint.value}"), ${constraint.number}, AllAvailableDevicesFiltered${count}), Constraint${count_constraint} = '';
        <#assign contraintnum = constraint.number?number - 1>
        <#list contraintnum..0 as gte_satisfiable>
        (
        \+check_gte(property("${constraint.key}", "${constraint.value}"), ${constraint.number}, _),
        check_gte(property("${constraint.key}", "${constraint.value}"), ${gte_satisfiable}, AllAvailableDevicesFiltered${count}),
        Constraint${count_constraint} = '[GEQ] ${distribution.name} ${constraint.key} ${constraint.value} >= ${constraint.number} (${gte_satisfiable} would be satisfiable)'
        )<#sep>;</#sep>
        </#list>
        ),
        <#assign count_constraint++>
    </#list>

    true)), 

    % then constrains that check all equal
    <#list distribution.checkAllConstraints as constraint>
    check_include_all(property("${constraint.key}", "${constraint.value}"), AllAvailableDevicesFiltered, AllAvailableDevicesFiltered${count}),
    </#list>

    % bind result to target variable
    AllAvailableDevicesFiltered${count} = ${distribution.name}.

</#list>

<#-- -------------------------------- -->
<#--        DISTRIBUTION QUERY        -->
<#-- -------------------------------- -->

distribution(<#list ast.distributions as distribution>${distribution.name}<#sep>,</#sep></#list>, Dependencies) :-
    % retrieve possible lists of devices
<#list ast.distributions as distribution>
    (get_distribution_${distribution.name}(${distribution.name}); (!, false) ),
</#list>

    % apply incompatible checks
<#list ast.incompatibilities as incompatibilitiesList>
    <#list incompatibilitiesList as key, value>
    check_incompatible(${key}, ${value}),
    </#list>
</#list>

    % apply dependency checks
<#assign dep_num = 0>
    Dependencies0 = [],
<#list ast.dependencies as dependency>
    <#assign dep_num++>
    <#if dependency.type == "distinct">
    check_dependency_distinct(match_${dependency.location},${dependency.dependent},${dependency.dependency},${dependency.amount_at_least}, Dependencies${dep_num},"${dependency.dependent}","${dependency.dependency}"),
    <#else>
    check_dependency(match_${dependency.location},${dependency.dependent},${dependency.dependency},${dependency.amount_at_least}, Dependencies${dep_num},"${dependency.dependent}","${dependency.dependency}"),
    </#if>
    % aggregate dependencies
    <#assign dep_num++>
    append(Dependencies${dep_num-2},Dependencies${dep_num-1},Dependencies${dep_num}),
</#list>
    Dependencies = Dependencies${dep_num},
    % finishing query with a .
    1 == 1.

<#-- -------------------------------- -->
<#-- DISTRIBUTION QUERY (With Drops)  -->
<#-- -------------------------------- -->
distribution_suggest(<#list ast.distributions as distribution>${distribution.name}<#sep>,</#sep></#list>, DroppedConstraints, Dependencies) :-
    % retrieve possible lists of devices
<#assign current_constraint = 1>
<#list ast.distributions as distribution>
    <#assign total_constraints_this_distribution = distribution.equalConstraints?size + distribution.gteConstraints?size>
    (get_distribution_allow_drop_${distribution.name}(${distribution.name}<#if total_constraints_this_distribution gt 0><#list 1..total_constraints_this_distribution as i>,Constraint${current_constraint}<#assign current_constraint++></#list></#if>); (!, false) ),
</#list>

    % apply incompatible checks
<#list ast.incompatibilities as incompatibilitiesList>
    <#list incompatibilitiesList as key, value>
    (
    (check_incompatible(${key}, ${value}), Constraint${current_constraint} = ''); 
    (Constraint${current_constraint} = '[INCOMP] Incompatibility between "${key}" and "${value}"')
    <#assign current_constraint++>
    <#assign total_constraints++>
    ),
    </#list>
</#list>

    % apply dependency checks
<#assign dep_num = 0>
<#list ast.dependencies as dependency>
    <#assign dep_num++>
    <#if dependency.type == "distinct">
    (
        (
        check_dependency_distinct(match_${dependency.location},${dependency.dependent},${dependency.dependency},${dependency.amount_at_least},Dependencies${dep_num},"${dependency.dependent}","${dependency.dependency}"),
        Constraint${current_constraint} = ''
        );
        (
        <#assign contraintnum = dependency.amount_at_least?number - 1>
        <#list contraintnum..0 as dep_satisfiable>
        (
        check_dependency_distinct(match_${dependency.location},${dependency.dependent},${dependency.dependency},${dep_satisfiable},Dependencies${dep_num},"${dependency.dependent}","${dependency.dependency}"),
        Constraint${current_constraint} = '[DEP-DIST] "${dependency.dependent}" depends on at least ${dependency.amount_at_least} distinct instances of "${dependency.dependency}" (${dep_satisfiable} would be satisfiable)'
        )<#sep>;</#sep>
        </#list>
        )
    ),
    <#else>
    (
        (   
        check_dependency(match_${dependency.location},${dependency.dependent},${dependency.dependency},${dependency.amount_at_least},Dependencies${dep_num},"${dependency.dependent}","${dependency.dependency}"),
        Constraint${current_constraint} = ''
        );
        (
        <#assign contraintnum = dependency.amount_at_least?number - 1>
        <#list contraintnum..0 as dep_satisfiable>
        (
        check_dependency(match_${dependency.location},${dependency.dependent},${dependency.dependency},${dep_satisfiable},Dependencies${dep_num},"${dependency.dependent}","${dependency.dependency}"),
        Constraint${current_constraint} = '[DEP] "${dependency.dependent}" depends on at least ${dependency.amount_at_least} (possibly shared) instances of "${dependency.dependency}" (${dep_satisfiable} would be satisfiable)'
        )<#sep>;</#sep>
        </#list>
        )
    ),
    </#if>
    <#assign current_constraint++>
    <#assign total_constraints++>
</#list>

    % aggregate dependencies into list of lists & flatten
    NestedDependencies = [<#list 1..dep_num as i>Dependencies${i}<#sep>,</#sep></#list>],
    flatten(NestedDependencies, Dependencies),

    % collect dropped constraints

    DroppedConstraints0 = [],
    <#if total_constraints gt 0>
        <#list 1..total_constraints as i>
        (Constraint${i} == '', DroppedConstraints${i} = DroppedConstraints${i-1};
        (\+(Constraint${i} == ''), append(DroppedConstraints${i-1}, [Constraint${i}], DroppedConstraints${i}), write('Dropped constraint: '), writeln(Constraint${i}))),
        
        </#list>
    </#if>
    DroppedConstraints = DroppedConstraints${total_constraints},

    % finishing query with a .
    1 == 1.

