-- Create the view with category details and counts of items by status
CREATE
OR REPLACE VIEW "recollector"."category_item_counts" AS
SELECT "c"."category_id",
       "c"."user_id",
       "c"."category_name",
       "c"."created_at",
       "c"."updated_at",
       -- Count of items with status TODO_LATER
       COUNT(CASE WHEN "i"."item_status" = 'TODO_LATER' THEN 1 END)  AS "count_todo_later",
       -- Count of items with status IN_PROGRESS
       COUNT(CASE WHEN "i"."item_status" = 'IN_PROGRESS' THEN 1 END) AS "count_in_progress",
       -- Count of items with status FINISHED
       COUNT(CASE WHEN "i"."item_status" = 'FINISHED' THEN 1 END)    AS "count_finished"
FROM "recollector"."categories" "c"
         -- Left join with items to include categories even with no items
         LEFT JOIN "recollector"."items" "i" ON "c"."category_id" = "i"."category_id"
GROUP BY "c"."category_id",
         "c"."user_id",
         "c"."category_name",
         "c"."created_at",
         "c"."updated_at";
