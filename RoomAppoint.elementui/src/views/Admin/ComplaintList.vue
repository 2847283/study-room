<template>
    <div class="app-container">
        <el-card class="box-card">
            <div slot="header" class="clearfix" style="display: flex; align-items: center;">
                <span>投诉管理</span>
                  <div style="margin-left: auto;">
                  <el-button type="primary" size="mini" icon="el-icon-search" @click="SearchClick">搜 索</el-button>
                  <el-button type="warning" size="mini" @click="ResetClick" icon="el-icon-refresh">重 置</el-button>
                  </div>
            </div>
            <div class="tb-body">
                <el-form ref="searchFormRef" :model="searchForm" :inline="true" label-width="100px">
                    <el-form-item label="投诉状态" prop="ComplaintStatus">
                        <el-select v-model="searchForm.ComplaintStatus" placeholder="请选择状态" clearable>
                            <el-option label="待处理" :value="1"></el-option>
                            <el-option label="处理完成" :value="2"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="投诉用户" prop="UserId">
                        <SigleSelect url="/User/List" columnName="Name" :clearable="true" columnValue="Id"
                            v-model="searchForm.UserId">
                        </SigleSelect>
                    </el-form-item>
                    <el-form-item label="处理人" prop="AdminId">
                        <SigleSelect url="/User/List" columnName="Name" :clearable="true" columnValue="Id"
                            v-model="searchForm.AdminId" :where="{ IsAdmin: 1 }">
                        </SigleSelect>
                    </el-form-item>
                </el-form>
            </div>
        </el-card>

        <el-dialog :title="formData.Id ? '处理投诉' : '新增投诉'" :visible.sync="editorShow" width="60%" :lock-scroll="true">
            <el-form v-if="editorShow == true" ref="editModalForm" :rules="editModalFormRules" :model="formData"
                label-width="100px" size="mini">
                <el-row :gutter="10" class="EditFromBody">
                    <el-col :span="24">
                        <el-form-item label="投诉用户" prop="UserId" v-if="!formData.Id">
                            <SigleSelect url="/User/List" columnName="Name" columnValue="Id"
                                v-model="formData.UserId" :clearable="true">
                            </SigleSelect>
                        </el-form-item>
                        <el-form-item label="投诉用户" v-else>
                            <span>{{ formData.Name || '未知用户' }}</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="投诉内容" prop="content">
                            <el-input type="textarea" v-model.trim="formData.content" placeholder="请输入投诉内容"
                                :rows="4" :clearable="true" :readonly="!!formData.Id"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.Id">
                        <el-form-item label="处理状态" prop="ComplaintStatus">
                            <el-select v-model="formData.ComplaintStatus" placeholder="请选择处理状态" style="width:100%">
                                <el-option label="待处理" :value="1"></el-option>
                                <el-option label="处理完成" :value="2"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.Id">
                        <el-form-item label="处理回复" prop="Response">
                            <el-input type="textarea" v-model.trim="formData.Response" placeholder="请输入处理回复"
                                :rows="3" :clearable="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.Id">
                        <el-form-item label="处理人" prop="AdminId">
                            <SigleSelect url="/User/List" :where="{ IsAdmin: 1 }" columnName="Name" columnValue="Id"
                                v-model="formData.AdminId">
                            </SigleSelect>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row type="flex" justify="end" align="bottom">
                    <el-form-item>
                        <el-button type="primary" plain @click="CreateOrEditForm()">确 定</el-button>
                        <el-button @click="editorShow = false">取 消</el-button>
                    </el-form-item>
                </el-row>
            </el-form>
        </el-dialog>

        <!-- 投诉详情对话框 -->
        <el-dialog title="投诉详情" :visible.sync="detailShow" width="50%">
            <el-descriptions v-if="currentComplaint" :column="1" border>
                <el-descriptions-item label="投诉ID">{{ currentComplaint.Id }}</el-descriptions-item>
                <el-descriptions-item label="投诉用户">{{ currentComplaint.UserName || '未知用户' }}</el-descriptions-item>
                <el-descriptions-item label="投诉状态">
                    <el-tag :type="currentComplaint.ComplaintStatus === 1 ? 'danger' : 'success'">
                        {{ currentComplaint.ComplaintStatus === 1 ? '待处理' : '处理完成' }}
                    </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="投诉内容">
                    <div style="white-space: pre-line; background: #f9f9f9; padding: 10px; border-radius: 4px;">
                        {{ currentComplaint.content }}
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label="处理回复" v-if="currentComplaint.Response">
                    <div style="white-space: pre-line; background: #f0f9ff; padding: 10px; border-radius: 4px;">
                        {{ currentComplaint.Response }}
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label="处理人">{{ currentComplaint.AdminName || '未分配' }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ currentComplaint.CreationTime }}</el-descriptions-item>
            </el-descriptions>
        </el-dialog>

        <PaginationTable ref="PaginationTableId" url="/Complaint/List" :column="dataColum" :where="where">
            <template v-slot:header>
                <el-button type="primary" size="mini" plain icon="el-icon-edit" @click="ShowEditModal()">新 增 投 诉</el-button>
                <el-button type="danger" size="mini" plain icon="el-icon-delete" @click="ShowBatchDeleteModal()">批 量 删 除</el-button>
            </template>
            
            <template v-slot:operation="slotProps">
                <el-button type="text" size="mini" @click="ShowDetailModal(slotProps.row)">查看详情</el-button>
                <el-button type="text" size="mini" @click="ShowEditModal(slotProps.row.Id)">处理</el-button>
                <el-button type="text" size="mini" @click="ShowDeleteModal(slotProps.row.Id)" style="color: #F56C6C;">删除</el-button>
            </template>
        </PaginationTable>
    </div>
</template>

<script>
import store from '@/store';
import { mapGetters } from 'vuex'

export default {
    name: "ComplaintList",
    computed: {
        ...mapGetters([
            'Token', 'UserInfo', 'RoleType', 'HasUserInfo', 'ColumnType', "UserId"
        ])
    },
    data() {
        return {
            where: {},
            searchForm: {
                ComplaintStatus: '',
                UserId: '',
                AdminId: ''
            },
            dataColum: [
                {
                    key: "Id",
                    title: "投诉ID",
                    type: store.getters.ColumnType.SHORTTEXT,
                    width: 80
                },
                {
                    key: "UserId",
                    hidden: true,
                },
                {
                    key: "UserDto.Name",
                    title: "投诉用户",
                    type: store.getters.ColumnType.SHORTTEXT,
                    width: 120
                },
                {
                    key: "Content",
                    title: "投诉内容",
                    type: store.getters.ColumnType.SHORTTEXT,
                    formatter: (row) => {
                        return row.content && row.content.length > 50 ? 
                               row.content.substring(0, 50) + '...' : row.content;
                    }
                },
                {
                    key: "ComplaintStatusFormat",
                    title: "处理状态",
                    type: store.getters.ColumnType.SHORTTEXT,
                    width: 100,
                    formatter: (row) => {
                        return row.ComplaintStatus === 1 ? '待处理' : '处理完成';
                    }
                },
                {
                    key: "AdminId",
                    hidden: true,
                },
                {
                    key: "AdminDto.Name",
                    title: "处理人",
                    type: store.getters.ColumnType.SHORTTEXT,
                    width: 120
                },
                {
                    key: "Response",
                    title: "处理回复",
                    type: store.getters.ColumnType.SHORTTEXT,
                    formatter: (row) => {
                        return row.Response && row.Response.length > 30 ? 
                               row.Response.substring(0, 30) + '...' : row.Response || '-';
                    }
                },
                {
                    key: "CreationTime",
                    title: "创建时间",
                    type: store.getters.ColumnType.DATETIME,
                    width: 160
                },
                {
                    key: "operation",
                    title: "操作",
                    type: store.getters.ColumnType.OPERATION,
                    fixed: 'right',
                    width: 200
                }
            ],
            editModalFormRules: {
                "UserId": [
                    { required: true, message: '投诉用户为必填项', trigger: 'change' }
                ],
                "content": [
                    { required: true, message: '投诉内容为必填项', trigger: 'blur' }
                ],
                "ComplaintStatus": [
                    { required: true, message: '处理状态为必填项', trigger: 'change' }
                ],
                "AdminId": [
                    { required: true, message: '处理人为必填项', trigger: 'change' }
                ]
            },
            formData: {
                Id: 0,
                UserId: '',
                content: '',
                AdminId: '',
                ComplaintStatus: 1,
                Response: '',
                CreationTime: '',
                CreatorId: ''
            },
            editorShow: false,
            detailShow: false,
            currentComplaint: null
        };
    },
    created() {
        // 初始化时可以设置默认处理人为当前用户
        if (this.UserId) {
            this.formData.CreatorId = this.UserId;
            this.formData.AdminId = this.UserId;
        }
    },
    methods: {
        // 修改属性
        async UpdateEntityAsync(Id, title, data) {
            let { Data } = await this.$PostSigleUpdate(`/Complaint/Get`, `/Complaint/CreateOrEdit`, Id, title, data);
            this.$refs.PaginationTableId.Reload(this.searchForm);
        },
        
        /**
         * 点击新增或者编辑的时候会触发
         */
        async ShowEditModal(Id) {
            if (Id) {
                let { Data } = await this.$Post(`/Complaint/Get`, { Id: Id });
                this.formData = Data;
            } else {
                // 新增时重置表单
                this.formData = {
                    Id: 0,
                    UserId: '',
                    content: '',
                    AdminId: this.UserId, // 默认当前用户为处理人
                    ComplaintStatus: 1,
                    Response: '',
                    CreationTime: new Date().toISOString().split('T')[0],
                    CreatorId: this.UserId
                };
            }
            this.editorShow = true;
        },
        
        /**
         * 查看投诉详情
         */
        async ShowDetailModal(row) {
            this.currentComplaint = row;
            this.detailShow = true;
        },
        
        /**
         * 点击保存的时候会触发
         */
        async CreateOrEditForm() {
            this.$refs.editModalForm.validate(async valid => {
                if (valid) {
                    // 设置创建时间
                    if (!this.formData.Id) {
                        this.formData.CreationTime = new Date().toISOString().split('T')[0];
                        this.formData.CreatorId = this.UserId;
                    }
                    
                    var { Success } = await this.$Post(`/Complaint/CreateOrEdit`, this.formData);
                    
                    if (Success) {
                        this.$message.success(this.formData.Id ? '处理成功' : '新增成功');
                        this.editorShow = false;
                        this.$refs.PaginationTableId.Reload(this.searchForm);
                    }
                }
            })
        },
        
        /**
         * 单个删除的时候会触发
         */
        async ShowDeleteModal(Id) {
            this.$confirm('确定要删除这条投诉记录吗?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(async () => {
                await this.$PostDelete(`/Complaint/Delete`, { Id: Id });
                this.$refs.PaginationTableId.Reload(this.searchForm);
                this.$message.success('删除成功');
            }).catch(() => {
                // 用户取消删除
            });
        },
        
        /**
         * 批量删除的时候会触发
         */
        async ShowBatchDeleteModal() {
            var ids = this.$refs.PaginationTableId.GetSelectionRow().map(x => x.Id);
            if (ids.length == 0) {
                this.$message.error("请选择需要删除的记录");
                return;
            }
            
            this.$confirm('确定要删除选中的投诉记录吗?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(async () => {
                await this.$PostDelete(`/Complaint/BatchDelete`, { Ids: ids });
                this.$refs.PaginationTableId.Reload(this.searchForm);
                this.$message.success('删除成功');
            }).catch(() => {
                // 用户取消删除
            });
        },
        
        /**
         * 点击表格搜索按钮会触发
         */
        async SearchClick() {
            this.$refs.PaginationTableId.Reload(this.searchForm);
        },
        
        /**
         * 点击清空表单会触发
         */
        async ResetClick() {
            this.searchForm = {
                ComplaintStatus: '',
                UserId: '',
                AdminId: ''
            };
            this.$refs.PaginationTableId.Reload(this.searchForm);
        }
    },
};
</script>

<style scoped>
.app-container {
    padding: 20px;
}

.box-card {
    margin-bottom: 20px;
}

.tb-body {
    display: flex;
    flex-wrap: wrap;
}

.EditFromBody {
    max-height: 60vh;
    overflow-y: auto;
    padding-right: 10px;
}

.status-badge {
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
}

.status-pending {
    background: #fff0f0;
    color: #f56c6c;
}

.status-resolved {
    background: #f0f9ff;
    color: #409eff;
}
</style>