create sequence if not exists core.ingredient_composition_composition_id_seq;

select setval(
    'core.ingredient_composition_composition_id_seq',
    coalesce((select max(composition_id) from core.ingredient_composition), 0) + 1,
    false
);

alter sequence core.ingredient_composition_composition_id_seq
    owned by core.ingredient_composition.composition_id;

alter table core.ingredient_composition
    alter column composition_id set default nextval('core.ingredient_composition_composition_id_seq');
