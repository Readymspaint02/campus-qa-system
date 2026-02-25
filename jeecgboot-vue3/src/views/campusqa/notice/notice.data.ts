import { BasicColumn, FormSchema } from '/@/components/Table';

const levelOptions = [
  { label: '高', value: 'H' },
  { label: '中', value: 'M' },
  { label: '低', value: 'L' },
];

const levelMap: Record<string, string> = {
  H: '高',
  M: '中',
  L: '低',
};

const statusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '已发布', value: 'published' },
  { label: '已过期', value: 'expired' },
];

const statusMap: Record<string, string> = {
  draft: '草稿',
  published: '已发布',
  expired: '已过期',
};

export const columns: BasicColumn[] = [
  {
    title: '标题',
    dataIndex: 'title',
    align: 'left',
  },
  {
    title: '级别',
    dataIndex: 'level',
    width: 100,
    customRender: ({ text }) => levelMap[String(text)] || text,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 120,
    customRender: ({ text }) => statusMap[String(text)] || text,
  },
  {
    title: '发布时间',
    dataIndex: 'publishTime',
    width: 180,
  },
  {
    title: '过期时间',
    dataIndex: 'expireTime',
    width: 180,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'title',
    label: '标题',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'status',
    label: '状态',
    component: 'Select',
    componentProps: {
      options: statusOptions,
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
    field: 'title',
    label: '标题',
    component: 'Input',
    required: true,
  },
  {
    field: 'content',
    label: '内容',
    component: 'InputTextArea',
    required: true,
  },
  {
    field: 'deptCode',
    label: '部门编码',
    component: 'Input',
  },
  {
    field: 'level',
    label: '级别',
    component: 'Select',
    componentProps: {
      options: levelOptions,
    },
  },
  {
    field: 'status',
    label: '状态',
    component: 'Select',
    componentProps: {
      options: statusOptions,
    },
  },
  {
    field: 'publishTime',
    label: '发布时间',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
  },
  {
    field: 'expireTime',
    label: '过期时间',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
  },
  {
    field: 'attachments',
    label: '附件',
    component: 'InputTextArea',
  },
];
