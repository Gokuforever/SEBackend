package com.sorted.commons.helper;

import com.sorted.commons.config.StaticMongoAccessor;
import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.enums.Operators;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter.*;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Base MongoDB repository interface that provides common CRUD and query operations for MongoDB entities.
 * This interface extends Spring's MongoRepository and adds custom methods for querying, filtering, 
 * and aggregation operations specific to MongoDB.
 *
 * @param <K> The type of the entity ID
 * @param <T> The entity type that extends BaseMongoEntity
 *
 * @author Sorted
 */
public interface BaseMongoRepository<K, T extends BaseMongoEntity<K>>
        extends BaseRepository<T, K>, MongoRepository<T, K> {

    /** Logger for this repository */
    Logger LOGGER = LoggerFactory.getLogger(BaseMongoRepository.class);

    /**
     * Returns the entity class type for the repository.
     * This method must be implemented by child interfaces.
     *
     * @return The class type of the entity
     * @throws UnsupportedOperationException if not overridden by the child interface
     */
    default Class<T> getEntityType() {
        throw new UnsupportedOperationException("This method must be overridden in the child interface.");
    }

    // =============== BASIC CRUD OPERATIONS ===============

    /**
     * Finds all entities in the repository.
     *
     * @return List of all entities
     */
    @Override
    default List<T> repoFindAll() {
        LOGGER.debug("Finding all entities");
        return this.findAll();
    }

    /**
     * Creates a new entity in the repository.
     *
     * @param obj The entity to create
     * @param cudBy User identifier for auditing
     * @return The created entity with its ID
     * @throws IllegalArgumentException if obj is null
     */
    @Override
    default T create(@NonNull T obj, String cudBy) {
        LOGGER.debug("Creating new entity with cudBy: {}", cudBy);
        obj.setBeforeCreate(cudBy);
        return this.insert(obj);
    }

    /**
     * Creates multiple entities in a bulk operation.
     *
     * @param list List of entities to create
     * @param cudBy User identifier for auditing
     * @return List of created entities with their IDs
     * @throws IllegalArgumentException if list is null
     */
    @Override
    default List<T> bulkCreate(@NonNull List<T> list, String cudBy) {
        LOGGER.debug("Bulk creating {} entities with cudBy: {}", list.size(), cudBy);
        List<T> newList = new ArrayList<>();
        for (T t : list) {
            t.setBeforeCreate(cudBy);
            newList.add(t);
        }
        return this.insert(newList);
    }

    /**
     * Updates an existing entity identified by its ID.
     *
     * @param id ID of the entity to update
     * @param obj Updated entity data
     * @param cudBy User identifier for auditing
     * @return The updated entity
     * @throws CustomIllegalArgumentsException if entity with given ID doesn't exist
     */
    @Override
    default T update(K id, T obj, String cudBy) {
        LOGGER.debug("Updating entity with id: {} by cudBy: {}", id, cudBy);
        Optional<T> optional = this.findById(id);
        if (optional.isEmpty()) {
            LOGGER.error("Entity with id {} not found for update", id);
            throw new CustomIllegalArgumentsException(ResponseCode.MISSING_ID);
        }
        obj.setBeforeModification(cudBy);
        return this.save(obj);
    }

    /**
     * Creates a new entity or updates an existing one if ID is provided.
     *
     * @param id ID of the entity to update (or null for create)
     * @param obj Entity data
     * @param cudBy User identifier for auditing
     * @return The created or updated entity
     */
    @Override
    default T upsert(K id, T obj, String cudBy) {
        LOGGER.debug("Upserting entity with id: {} by cudBy: {}", id, cudBy);
        if (id == null) {
            return this.create(obj, cudBy);
        }
        return update(id, obj, cudBy);
    }

    /**
     * Returns the total count of entities in the repository.
     *
     * @return Total count of entities
     */
    @Override
    default long totalCount() {
        LOGGER.debug("Counting total entities");
        return this.count();
    }

    /**
     * Finds a single entity matching the specified filter.
     *
     * @param filter Search and filter criteria
     * @return The matching entity or null if not found
     */
    @Override
    default T repoFindOne(SEFilter filter) {
        LOGGER.debug("Finding one entity with filter: {}", filter);
        Query query = buildQuery(filter);
        LOGGER.info("Query for findOne: {}", query);
        return StaticMongoAccessor.MONGO_TEMPLATE.findOne(query, getEntityType());
    }

    /**
     * Finds all entities matching the specified filter.
     *
     * @param filter Search and filter criteria
     * @return List of matching entities
     */
    @Override
    default List<T> repoFind(SEFilter filter) {
        LOGGER.debug("Finding entities with filter: {}", filter);
        Query query = buildQuery(filter);
        LOGGER.info("Query for find: {}", query);
        return StaticMongoAccessor.MONGO_TEMPLATE.find(query, getEntityType());
    }

    /**
     * Counts entities matching the specified filter.
     *
     * @param filter Search and filter criteria
     * @return Count of matching entities
     */
    @Override
    default long countByFilter(SEFilter filter) {
        LOGGER.debug("Counting entities with filter: {}", filter);
        Query query = buildQuery(filter);
        LOGGER.info("Query for count: {}", query);
        return StaticMongoAccessor.MONGO_TEMPLATE.count(query, getEntityType());
    }

    /**
     * Performs a soft delete on an entity by setting its deleted flag to true.
     *
     * @param id ID of the entity to delete
     * @param cudBy User identifier for auditing
     */
    @Override
    default void deleteOne(K id, String cudBy) {
        LOGGER.debug("Soft deleting entity with id: {} by cudBy: {}", id, cudBy);
        Optional<T> optional = this.findById(id);
        if (optional.isEmpty()) {
            LOGGER.warn("Entity with id {} not found for deletion", id);
            return;
        }
        T t = optional.get();
        t.setDeleted(true);
        t.setBeforeModification(cudBy);
        this.save(t);
    }

    // =============== QUERY BUILDING METHODS ===============

    /**
     * Builds a MongoDB Query object from an SEFilter specification.
     *
     * @param filter The filter specification
     * @return A MongoDB Query object
     */
    public static Query buildQuery(SEFilter filter) {
        Criteria criteria = buildCriteria(filter);
        Query query = new Query(criteria);

        // Apply ordering if specified
        if (filter.getOrderBy() != null) {
            query.with(buildSort(filter.getOrderBy()));
        }

        // Apply field selection if specified
        if (filter.getSelection() != null && !filter.getSelection().isEmpty()) {
            query.fields().include(filter.getSelection().toArray(new String[0]));
        }

        // Apply pagination if specified
        if (filter.getPagination() != null) {
            Pageable pageable = PageRequest.of(filter.getPagination().getPage(), filter.getPagination().getSize());
            query.with(pageable);
        }

        return query;
    }

    /**
     * Builds MongoDB Criteria from an SEFilter.
     *
     * @param filter The filter specification
     * @return MongoDB Criteria object
     * @throws CustomIllegalArgumentsException if no valid criteria could be built
     */
    private static Criteria buildCriteria(SEFilter filter) {
        List<WhereClause> clauses = filter.getClause();
        SEFilterType baseType = filter.getType();

        Criteria criteria = new Criteria();
        Criteria subQueryCriteria = new Criteria();
        List<Criteria> baseQueryCriterias = new ArrayList<>();
        List<Criteria> nodeCriterias = new ArrayList<>();

        // Process where clauses
        if (!CollectionUtils.isEmpty(clauses)) {
            baseQueryCriterias = clauses.stream()
                    .map(BaseMongoRepository::buildWhereClauseCriteria)
                    .collect(Collectors.toList());
        }

        // Process filter nodes
        if (!CollectionUtils.isEmpty(filter.getNodes())) {
            nodeCriterias = filter.getNodes().stream()
                    .map(BaseMongoRepository::buildCriteria)
                    .toList();
        }

        Criteria[] arrComb;

        // Combine node criteria and base criteria
        if (!nodeCriterias.isEmpty() && !baseQueryCriterias.isEmpty()) {
            if (nodeCriterias.size() > 1) {
                Criteria[] subQueriesComb = nodeCriterias.toArray(new Criteria[0]);
                subQueryCriteria.orOperator(subQueriesComb);
                baseQueryCriterias.add(subQueryCriteria);
            } else {
                baseQueryCriterias.addAll(nodeCriterias);
            }
            arrComb = baseQueryCriterias.toArray(new Criteria[0]);
        } else if (!baseQueryCriterias.isEmpty()) {
            arrComb = baseQueryCriterias.toArray(new Criteria[0]);
        } else if (!nodeCriterias.isEmpty()) {
            arrComb = nodeCriterias.toArray(new Criteria[0]);
        } else {
            LOGGER.error("No valid criteria to build from filter: {}", filter);
            throw new CustomIllegalArgumentsException(ResponseCode.ERR_0001);
        }

        // Apply AND or OR operator based on filter type
        if (baseType.equals(SEFilterType.AND)) {
            criteria.andOperator(arrComb);
        } else if (baseType.equals(SEFilterType.OR)) {
            criteria.orOperator(arrComb);
        }

        return criteria;
    }

    /**
     * Builds criteria from a filter node.
     *
     * @param node The filter node
     * @return MongoDB Criteria
     */
    private static Criteria buildCriteria(SEFilterNode node) {
        Criteria criteria = new Criteria();

        if (!CollectionUtils.isEmpty(node.getClause())) {
            List<Criteria> whereCriterias = node.getClause().stream()
                    .map(BaseMongoRepository::buildWhereClauseCriteria)
                    .toList();

            criteria.andOperator(whereCriterias.toArray(new Criteria[0]));
        }

        return criteria;
    }

    /**
     * Builds a MongoDB Criteria object from a where clause.
     *
     * @param clause The where clause
     * @return MongoDB Criteria
     */
    private static Criteria buildWhereClauseCriteria(WhereClause clause) {
        Operators relation = clause.getOperator();

        switch (relation) {
            case LIKE:
                String valueL = Pattern.quote(clause.getValue());
                String regexL = String.format(".*(%s).*", valueL);
                return new Criteria(clause.getField()).regex(regexL, "i");

            case NOT_LIKE:
                String valueNL = Pattern.quote(clause.getValue());
                String regexNL = String.format(".*(%s).*", valueNL);
                return new Criteria(clause.getField()).not().regex(regexNL, "i");

            case EQUALS:
                return new Criteria(clause.getField()).is(clause.getValueAsObject());

            case NOT_EQUALS:
                return new Criteria(clause.getField()).ne(clause.getValueAsObject());

            case IN:
                return new Criteria(clause.getField()).in(clause.getValueList());

            case NIN:
                return new Criteria(clause.getField()).nin(clause.getValueList());

            case ALL:
                return new Criteria(clause.getField()).all(clause.getValueList());

            case GT:
                return new Criteria(clause.getField()).gt(clause.getValueAsObject());

            case LT:
                return new Criteria(clause.getField()).lt(clause.getValueAsObject());

            case GTE:
                return new Criteria(clause.getField()).gte(clause.getValueAsObject());

            case LTE:
                return new Criteria(clause.getField()).lte(clause.getValueAsObject());

            case ELEMMATCH_IN:
                String keyName = clause.getField();
                Criteria elemMatchCriteria = new Criteria();
                Map<String, Object> elemMap = clause.getElemMap();

                for (Map.Entry<String, Object> entry : elemMap.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof List<?>) {
                        // Use 'in' for list values
                        elemMatchCriteria = elemMatchCriteria.and(key).in((List<?>) value);
                    } else {
                        // Use 'is' for single values
                        elemMatchCriteria = elemMatchCriteria.and(key).is(value);
                    }
                }

                return new Criteria(keyName).elemMatch(elemMatchCriteria);

            default:
                LOGGER.warn("Unsupported operator: {}", relation);
                return null;
        }
    }

    /**
     * Builds a Sort object from an OrderBy specification.
     *
     * @param orderBy The ordering specification
     * @return Spring Data Sort object
     */
    private static Sort buildSort(OrderBy orderBy) {
        Sort.Direction direction = orderBy.getType() == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, orderBy.getKey());
    }

    // =============== AGGREGATION OPERATIONS ===============

    /**
     * Performs an aggregation operation with lookup (join) support.
     *
     * @param filter      The base filter for the main collection
     * @param lookups     List of lookup operations to join with other collections
     * @param projections Fields to include in the result (null for all fields)
     * @return List of aggregation results as maps
     */
    default List<Map<String, Object>> aggregateWithLookup(SEFilter filter,
                                                          List<AggregationLookup> lookups,
                                                          List<String> projections) {
        LOGGER.debug("Performing aggregation with lookup - filter: {}, lookups: {}", filter, lookups);
        List<AggregationOperation> operations = new ArrayList<>();

        // Add match operation if filter is provided
        if (filter != null) {
            Criteria criteria = buildCriteria(filter);
            operations.add(Aggregation.match(criteria));
        }

        // Add lookup operations
        if (lookups != null && !lookups.isEmpty()) {
            for (AggregationLookup lookup : lookups) {
                LookupOperation lookupOp = Aggregation.lookup(
                        lookup.getFromCollection(),
                        lookup.getLocalField(),
                        lookup.getForeignField(),
                        lookup.getAs()
                );
                operations.add(lookupOp);
            }
        }

        // Add sort if provided in filter
        if (filter != null && filter.getOrderBy() != null) {
            Sort sort = buildSort(filter.getOrderBy());
            operations.add(Aggregation.sort(sort));
        }

        // Add projection if fields are specified
        if (projections != null && !projections.isEmpty()) {
            ProjectionOperation projection = Aggregation.project(projections.toArray(new String[0]));
            operations.add(projection);
        }

        // Add pagination if provided in filter
        if (filter != null && filter.getPagination() != null) {
            operations.add(Aggregation.skip((long) filter.getPagination().getPage() * filter.getPagination().getSize()));
            operations.add(Aggregation.limit(filter.getPagination().getSize()));
        }

        Aggregation aggregation = Aggregation.newAggregation(operations);
        LOGGER.debug("Aggregation pipeline: {}", aggregation);

        AggregationResults<Map> results = StaticMongoAccessor.MONGO_TEMPLATE.aggregate(
                aggregation,
                StaticMongoAccessor.MONGO_TEMPLATE.getCollectionName(getEntityType()),
                Map.class
        );

        return new ArrayList<>(results.getMappedResults());
    }

    /**
     * Performs a simple aggregation operation with a single lookup (join).
     *
     * @param filter         The base filter
     * @param fromCollection Collection to join with
     * @param localField     Field from main collection
     * @param foreignField   Field from joined collection
     * @param as             Alias for the joined data
     * @return List of aggregation results as maps
     */
    default List<Map<String, Object>> simpleLookup(SEFilter filter,
                                                   String fromCollection,
                                                   String localField,
                                                   String foreignField,
                                                   String as) {
        LOGGER.debug("Performing simple lookup - from: {}, localField: {}, foreignField: {}, as: {}",
                fromCollection, localField, foreignField, as);

        AggregationLookup lookup = new AggregationLookup(fromCollection, localField, foreignField, as);
        return aggregateWithLookup(filter, List.of(lookup), null);
    }

    /**
     * Performs a group by aggregation with optional lookups.
     *
     * @param filter       Base filter
     * @param groupFields  Fields to group by
     * @param accumulators Accumulation operations (sum, avg, etc.)
     * @param lookups      Optional lookups before grouping
     * @return List of aggregation results as maps
     */
    default List<Map<String, Object>> groupByAggregation(SEFilter filter,
                                                         List<String> groupFields,
                                                         Map<String, AggregationAccumulator> accumulators,
                                                         List<AggregationLookup> lookups) {
        LOGGER.debug("Performing group by aggregation - groupFields: {}, accumulators: {}", groupFields, accumulators);
        List<AggregationOperation> operations = new ArrayList<>();

        // Add match operation if filter is provided
        if (filter != null) {
            Criteria criteria = buildCriteria(filter);
            operations.add(Aggregation.match(criteria));
        }

        // Add lookup operations before grouping
        if (lookups != null && !lookups.isEmpty()) {
            for (AggregationLookup lookup : lookups) {
                operations.add(Aggregation.lookup(
                        lookup.getFromCollection(),
                        lookup.getLocalField(),
                        lookup.getForeignField(),
                        lookup.getAs()
                ));
            }
        }

        // Build group operation
        if (groupFields != null && !groupFields.isEmpty()) {
            String[] fields = groupFields.toArray(new String[0]);
            GroupOperation groupOperation = Aggregation.group(fields);

            // Add accumulators
            if (accumulators != null) {
                for (Map.Entry<String, AggregationAccumulator> entry : accumulators.entrySet()) {
                    String fieldName = entry.getKey();
                    AggregationAccumulator accumulator = entry.getValue();

                    groupOperation = switch (accumulator.getOperation()) {
                        case SUM -> groupOperation.sum(accumulator.getField()).as(fieldName);
                        case AVG -> groupOperation.avg(accumulator.getField()).as(fieldName);
                        case COUNT -> groupOperation.count().as(fieldName);
                        case MAX -> groupOperation.max(accumulator.getField()).as(fieldName);
                        case MIN -> groupOperation.min(accumulator.getField()).as(fieldName);
                    };
                }
            }

            operations.add(groupOperation);
        }

        // Add sort if provided in filter
        if (filter != null && filter.getOrderBy() != null) {
            Sort sort = buildSort(filter.getOrderBy());
            operations.add(Aggregation.sort(sort));
        }

        // Add pagination if provided in filter
        if (filter != null && filter.getPagination() != null) {
            operations.add(Aggregation.skip((long) filter.getPagination().getPage() * filter.getPagination().getSize()));
            operations.add(Aggregation.limit(filter.getPagination().getSize()));
        }

        Aggregation aggregation = Aggregation.newAggregation(operations);
        LOGGER.debug("Group aggregation pipeline: {}", aggregation);

        AggregationResults<Map> results = StaticMongoAccessor.MONGO_TEMPLATE.aggregate(
                aggregation,
                StaticMongoAccessor.MONGO_TEMPLATE.getCollectionName(getEntityType()),
                Map.class
        );

        return new ArrayList<>(results.getMappedResults());
    }

    /**
     * Performs a multi-stage lookup across multiple collections.
     *
     * @param filter      The base filter for the main collection
     * @param lookupChain Ordered list of lookups to be performed sequentially
     * @param projections Fields to include in the final result
     * @return List of aggregation results with nested joined data
     */
    default List<Map<String, Object>> multiCollectionLookup(SEFilter filter,
                                                            List<AggregationLookupChain> lookupChain,
                                                            List<String> projections) {
        LOGGER.debug("Performing multi-collection lookup with {} chains",
                lookupChain != null ? lookupChain.size() : 0);
        List<AggregationOperation> operations = new ArrayList<>();

        // Add match operation if filter is provided
        if (filter != null) {
            Criteria criteria = buildCriteria(filter);
            operations.add(Aggregation.match(criteria));
        }

        // Process each lookup chain
        if (lookupChain != null && !lookupChain.isEmpty()) {
            for (AggregationLookupChain chain : lookupChain) {
                // First lookup in the chain
                LookupOperation initialLookup = Aggregation.lookup(
                        chain.getFromCollection(),
                        chain.getLocalField(),
                        chain.getForeignField(),
                        chain.getAs()
                );
                operations.add(initialLookup);

                // Process nested lookups if any
                if (chain.getNestedLookups() != null && !chain.getNestedLookups().isEmpty()) {
                    for (NestedLookup nestedLookup : chain.getNestedLookups()) {
                        // Create the nested lookup path
                        String nestedPath = chain.getAs() + "." + nestedLookup.getLocalField();

                        // Add unwind operation if needed to flatten the array
                        if (nestedLookup.isUnwindBeforeLookup()) {
                            operations.add(Aggregation.unwind("$" + chain.getAs()));
                        }

                        // Add the nested lookup
                        LookupOperation nestedLookupOp = Aggregation.lookup(
                                nestedLookup.getFromCollection(),
                                nestedPath,
                                nestedLookup.getForeignField(),
                                nestedLookup.getAs()
                        );
                        operations.add(nestedLookupOp);
                    }
                }
            }
        }

        // Add sort if provided in filter
        if (filter != null && filter.getOrderBy() != null) {
            Sort sort = buildSort(filter.getOrderBy());
            operations.add(Aggregation.sort(sort));
        }

        // Add projection if fields are specified
        if (projections != null && !projections.isEmpty()) {
            ProjectionOperation projection = Aggregation.project(projections.toArray(new String[0]));
            operations.add(projection);
        }

        // Add pagination if provided in filter
        if (filter != null && filter.getPagination() != null) {
            operations.add(Aggregation.skip((long) filter.getPagination().getPage() * filter.getPagination().getSize()));
            operations.add(Aggregation.limit(filter.getPagination().getSize()));
        }

        Aggregation aggregation = Aggregation.newAggregation(operations);
        LOGGER.debug("Multi-collection lookup pipeline: {}", aggregation);

        AggregationResults<Map> results = StaticMongoAccessor.MONGO_TEMPLATE.aggregate(
                aggregation,
                StaticMongoAccessor.MONGO_TEMPLATE.getCollectionName(getEntityType()),
                Map.class
        );

        return new ArrayList<>(results.getMappedResults());
    }

    /**
     * Performs a graph-like traversal across collections.
     *
     * @param filter         The base filter
     * @param rootCollection Starting collection
     * @param traversalPath  List of edges to traverse in the graph
     * @param maxDepth       Maximum depth for recursive lookups (to prevent infinite loops)
     * @return List of results with graph traversal data
     */
    default List<Map<String, Object>> graphTraversal(SEFilter filter,
                                                     String rootCollection,
                                                     List<GraphEdge> traversalPath,
                                                     int maxDepth) {
        LOGGER.debug("Performing graph traversal - rootCollection: {}, maxDepth: {}", rootCollection, maxDepth);

        if (maxDepth <= 0 || traversalPath == null || traversalPath.isEmpty()) {
            LOGGER.warn("Invalid graph traversal parameters - returning empty result");
            return Collections.emptyList();
        }

        List<AggregationOperation> operations = new ArrayList<>();

        // Add match operation if filter is provided
        if (filter != null) {
            Criteria criteria = buildCriteria(filter);
            operations.add(Aggregation.match(criteria));
        }

        // Process each edge in the traversal path
        for (int i = 0; i < traversalPath.size() && i < maxDepth; i++) {
            GraphEdge edge = traversalPath.get(i);

            // Add lookup for this edge
            LookupOperation lookupOp = Aggregation.lookup(
                    edge.getTargetCollection(),
                    edge.getSourceField(),
                    edge.getTargetField(),
                    edge.getAs()
            );
            operations.add(lookupOp);

            // Add unwind if needed
            if (edge.isUnwindAfterLookup()) {
                operations.add(Aggregation.unwind("$" + edge.getAs(), true));
            }
        }

        // Add sort if provided in filter
        if (filter != null && filter.getOrderBy() != null) {
            Sort sort = buildSort(filter.getOrderBy());
            operations.add(Aggregation.sort(sort));
        }

        // Add pagination if provided in filter
        if (filter != null && filter.getPagination() != null) {
            operations.add(Aggregation.skip((long) filter.getPagination().getPage() * filter.getPagination().getSize()));
            operations.add(Aggregation.limit(filter.getPagination().getSize()));
        }

        Aggregation aggregation = Aggregation.newAggregation(operations);
        LOGGER.debug("Graph traversal pipeline: {}", aggregation);

        AggregationResults<Map> results = StaticMongoAccessor.MONGO_TEMPLATE.aggregate(
                aggregation,
                rootCollection,
                Map.class
        );

        return new ArrayList<>(results.getMappedResults());
    }
}