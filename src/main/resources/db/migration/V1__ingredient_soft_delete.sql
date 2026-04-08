alter table core.ingredient
    add column if not exists deleted boolean not null default false;
