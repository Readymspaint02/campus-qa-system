import { BasicColumn, FormSchema } from '/@/components/Table';

const typeOptions = [
  { label: '问答', value: 'qa' },
  { label: '通知', value: 'notice' },
  { label: '指南', value: 'guide' },
];

const typeMap: Record<string, string> = {
  qa: '问答',
  notice: '通知',
  guide: '指南',
};

const statusOptions = [
  { label: '启用', value: 'enable' },
  { label: '停用', value: 'disable' },
];

const statusMap: Record<string, string> = {
  enable: '启用',
  disable: '停用',
};

export const columns: BasicColumn[] = [
  {
    title: '名称',
    dataIndex: 'name',
    align: 'left',
  },
  {
    title: '类型',
    dataIndex: 'type',
    width: 120,
    customRender: ({ text }) => typeMap[String(text)] || text,
  },
  {
    title: '排序',
    dataIndex: 'sort',
    width: 80,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 120,
    customRender: ({ text }) => statusMap[String(text)] || text,
  },
  {
    title: '备注',
    dataIndex: 'remark',
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 180,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'name',
    label: '名称',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'type',
    label: '类型',
    component: 'Select',
    componentProps: {
      options: typeOptions,
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
    field: 'name',
    label: '名称',
    component: 'Input',
    required: true,
  },
  {
    field: 'type',
    label: '类型',
    component: 'Select',
    required: true,
    componentProps: {
      options: typeOptions,
    },
  },
  {
    field: 'parentId',
    label: '上级ID',
    component: 'Input',
  },
  {
    field: 'sort',
    label: '排序',
    component: 'InputNumber',
    componentProps: {
      min: 0,
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
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
  },
];
