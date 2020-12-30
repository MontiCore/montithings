:- include('facts').
:- include('helpers').

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

distribution(<#list ast.distributions as distribution>${distribution.name}<#sep>,</#sep></#list>) :-
    % retrieve possible lists of devices
<#list ast.distributions as distribution>
    get_distribution_${distribution.name}(${distribution.name}),
</#list>

    % apply incompatible checks
<#list ast.incompatibilities as incompatibilitiesList>
    <#list incompatibilitiesList as key, value>
    check_incompatible(${key}, ${value}),
    </#list>
</#list>

    % apply dependency checks
<#list ast.dependencies as dependency>
    <#if dependency.type == "distinct">
    check_dependency_distinct(${dependency.dependent},${dependency.dependency},${dependency.amount_at_least}),
    <#else>
    check_dependency(${dependency.dependent},${dependency.dependency},${dependency.amount_at_least}),
    </#if>
</#list>
    % finishing query with a .
    1 == 1.

any_distribution(<#list ast.distributions as distribution>${distribution.name}<#sep>,</#sep></#list>) :-
    % retrieve possible lists of devices
<#list ast.distributions as distribution>
    get_distribution_${distribution.name}(${distribution.name}), !,
</#list>

    % apply incompatible checks
<#list ast.incompatibilities as incompatibilitiesList>
    <#list incompatibilitiesList as key, value>
    check_incompatible(${key}, ${value}),
    </#list>
</#list>

    % apply dependency checks
<#list ast.dependencies as dependency>
    <#if dependency.type == "distinct">
    check_dependency_distinct(${dependency.dependent},${dependency.dependency},${dependency.amount_at_least}),
    <#else>
    check_dependency(${dependency.dependent},${dependency.dependency},${dependency.amount_at_least}),
    </#if>
</#list>
    % finishing query with a .
    1 == 1.