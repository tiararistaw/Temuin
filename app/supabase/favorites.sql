create table public.favorites (
  user_id    uuid not null default auth.uid()
             references auth.users(id) on delete cascade,
  report_id  text not null
             references public.reports(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (user_id, report_id)
);

alter table public.favorites enable row level security;

create policy "favorites_select_own" on public.favorites
  for select to authenticated using (user_id = (select auth.uid()));
create policy "favorites_insert_own" on public.favorites
  for insert to authenticated with check (user_id = (select auth.uid()));
create policy "favorites_delete_own" on public.favorites
  for delete to authenticated using (user_id = (select auth.uid()));

grant select, insert, delete on public.favorites to authenticated;