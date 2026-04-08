create table if not exists core.ingredient_solubility (
    solubility_id bigserial primary key,
    ingredient_id bigint not null references core.ingredient(ingredient_id) on delete cascade,
    medium_type text not null,
    solubility_class text,
    solubility_text text,
    concentration_text text,
    temperature_c numeric(10, 2),
    source_system text,
    source_sheet text,
    source_row_num integer,
    created_at timestamptz not null default now()
);

create index if not exists ix_ingredient_solubility_ingredient
    on core.ingredient_solubility(ingredient_id);

create index if not exists ix_ingredient_solubility_medium
    on core.ingredient_solubility(medium_type);

create table if not exists core.ingredient_solvent (
    ingredient_solvent_id bigserial primary key,
    ingredient_id bigint not null references core.ingredient(ingredient_id) on delete cascade,
    solvent_name text not null,
    solvent_name_norm text,
    note text,
    source_system text,
    source_sheet text,
    source_row_num integer,
    created_at timestamptz not null default now()
);

create index if not exists ix_ingredient_solvent_ingredient
    on core.ingredient_solvent(ingredient_id);

create index if not exists ix_ingredient_solvent_name_norm
    on core.ingredient_solvent(solvent_name_norm);

create table if not exists core.ingredient_wax_property (
    wax_property_id bigserial primary key,
    ingredient_id bigint not null references core.ingredient(ingredient_id) on delete cascade,
    property_type text not null
        constraint ingredient_wax_property_type_check
            check (property_type in ('dropping_point', 'melting_point')),
    value_num numeric(10, 2),
    unit_name text not null default 'degC',
    value_text text,
    method_text text,
    source_system text,
    source_sheet text,
    source_row_num integer,
    created_at timestamptz not null default now()
);

create index if not exists ix_ingredient_wax_property_ingredient
    on core.ingredient_wax_property(ingredient_id);

create index if not exists ix_ingredient_wax_property_type
    on core.ingredient_wax_property(property_type);
