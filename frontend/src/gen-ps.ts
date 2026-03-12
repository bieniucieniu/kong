import { column, Schema, Table } from '@powersync/web';
// OR: import { column, Schema, Table } from '@powersync/react-native';

const chat_message = new Table(
  {
    // id column (text) is automatically included
    session_id: column.text,
    role: column.text,
    content: column.text,
    created_at: column.text
  },
  { indexes: {} }
);

export const AppSchema = new Schema({
  chat_message
});

export type Database = (typeof AppSchema)['types'];
