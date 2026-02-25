import { BasicColumn, FormSchema } from '/@/components/Table';

const ratingOptions = [
  { label: '好评', value: 'like' },
  { label: '差评', value: 'dislike' },
];

const ratingMap: Record<string, string> = {
  like: '好评',
  dislike: '差评',
};

export const columns: BasicColumn[] = [
  {
    title: '用户ID',
    dataIndex: 'userId',
    width: 160,
  },
  {
    title: '问答ID',
    dataIndex: 'knowledgeId',
    width: 160,
  },
  {
    title: '评价',
    dataIndex: 'rating',
    width: 120,
    customRender: ({ text }) => ratingMap[String(text)] || text,
  },
  {
    title: '已处理',
    dataIndex: 'handled',
    width: 100,
    customRender: ({ text }) => (Number(text) === 1 ? '是' : '否'),
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'userId',
    label: '用户ID',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'rating',
    label: '评价',
    component: 'Select',
    componentProps: {
      options: ratingOptions,
    },
    colProps: { span: 8 },
  },
];

export const formSchema: FormSchema[] = [
  {
    label: '编号',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    field: 'knowledgeId',
    label: '问答ID',
    component: 'Input',
  },
  {
    field: 'userId',
    label: '用户ID',
    component: 'Input',
  },
  {
    field: 'rating',
    label: '评价',
    component: 'Select',
    componentProps: {
      options: ratingOptions,
    },
  },
  {
    field: 'content',
    label: '反馈内容',
    component: 'InputTextArea',
  },
  {
    field: 'reply',
    label: '回复内容',
    component: 'InputTextArea',
  },
  {
    field: 'handled',
    label: '已处理',
    component: 'Switch',
    componentProps: {
      checkedValue: 1,
      unCheckedValue: 0,
    },
  },
];
