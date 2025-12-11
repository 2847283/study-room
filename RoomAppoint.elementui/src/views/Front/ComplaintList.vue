<template>
    <div class="app-container">
        <el-page-header class="card margin-top-lg" @back="goBack" :content="'投诉与反馈'">
        </el-page-header>

        <!-- 提交投诉表单 -->
        <el-card class="box-card margin-top-lg">
            <div slot="header" class="clearfix">
                <span class="card-title">提交投诉</span>
            </div>
            <div class="tb-body">
                <el-form ref="complaintFormRef" :model="complaintForm" :rules="complaintRules" label-width="100px">
                    <el-form-item label="投诉内容" prop="Content" required>
                        <el-input
                            v-model="complaintForm.Content"
                            type="textarea"
                            :rows="4"
                            placeholder="请输入您的投诉内容，我们将尽快处理..."
                            :maxlength="500"
                            show-word-limit
                        ></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="submitComplaint" :loading="submitting">提交投诉</el-button>
                        <el-button @click="resetComplaintForm">重置</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </el-card>

        <!-- 投诉记录表格 -->
        <el-card class="box-card margin-top-lg">
            <div slot="header" class="clearfix">
                <span class="card-title">我的投诉记录</span>
                <el-button type="primary" size="mini" icon="el-icon-refresh" @click="refreshComplaints" style="float: right">刷 新</el-button>
            </div>
            
            <!-- 投诉记录表格部分修改 -->
<PaginationTable 
    ref="PaginationTableId" 
    url="/Complaint/List" 
    :column="dataColumn" 
    :where="where"
>
    
    <template v-slot:Operate="scope">
        <el-button type="primary" size="mini" @click="showDetail(scope.row)">查看详情</el-button>
    </template>
</PaginationTable>
        </el-card>

        <!-- 投诉详情对话框 -->
        <el-dialog 
            title="投诉详情" 
            :visible.sync="detailDialogVisible" 
            width="600px"
            :close-on-click-modal="false"
        >
            <div class="complaint-detail">
                <div class="detail-section">
                    <div class="detail-label">流水编号：</div>
                    <div class="detail-value">{{ currentComplaint.Id }}</div>
                </div>
                
                <div class="detail-section">
                    <div class="detail-label">投诉内容：</div>
                    <div class="detail-value detail-content">{{ currentComplaint.Content || '无' }}</div>
                </div>
                
                <div class="detail-section">
                    <div class="detail-label">处理状态：</div>
                    <div class="detail-value detail-content">{{ currentComplaint.ComplaintStatusFormat}}</div>
                </div>
                
                <div class="detail-section" v-if="currentComplaint.AdminDto">
                    <div class="detail-label">处理人：</div>
                    <div class="detail-value">{{ currentComplaint.AdminDto.Name}}</div>
                </div>
                
                <div class="detail-section" v-if="currentComplaint.Response">
                    <div class="detail-label">处理回复：</div>
                    <div class="detail-value detail-content">{{ currentComplaint.Response }}</div>
                </div>
                
            </div>
            
            <span slot="footer" class="dialog-footer">
                <el-button @click="detailDialogVisible = false">关 闭</el-button>
            </span>
        </el-dialog>
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
            // 查询条件
            where: {
                UserId: store.getters.UserId
            },
            
            // 投诉表单数据
            complaintForm: {
                Content: '',
                UserId: store.getters.UserId,
                AdminId: 1
            },
            
            // 表单验证规则
            complaintRules: {
                Content: [
                    { required: true, message: '请输入投诉内容', trigger: 'blur' },
                    { min: 10, message: '投诉内容至少10个字符', trigger: 'blur' }
                ]
            },
            
            // 表格列定义 - 简化配置
            dataColumn: [
                {
                    key: "Id",
                    title: "流水编号",
                    width: 100
                },
                {
                    key: "Content",
                    title: "投诉内容",
                    width: 300,
                    type: store.getters.ColumnType.SHORTTEXT,
                },
                {
                    key: "AdminDto.Name",
                    title: "处理人",
                    width: 130
                },
                {
                    key: "ComplaintStatusFormat",
                    title: "处理状态",
                    width: 150,
                },
                {
                    key: "Response",
                    title: "处理回复",
                    width: 300,
                    type: store.getters.ColumnType.SHORTTEXT,
                },
                {
                    key: "Operate",
                    title: "操作",
                    width: 100,
                    type: store.getters.ColumnType.USERDEFINED,
                }
            ],
            
            
            // 当前查看的投诉记录
            currentComplaint: {},
            
            // 详情对话框显示状态
            detailDialogVisible: false,
            
            // 提交状态
            submitting: false
        };
    },
    created() {
    },
    methods: {
        // 返回上一页
        goBack() {
            this.$router.go(-1)
        },
        // 提交投诉
        async submitComplaint() {
            this.$refs.complaintFormRef.validate(async (valid) => {
                if (valid) {
                    this.submitting = true;
                    try {
                        // 调用后端接口提交投诉
                        const { Success, Message } = await this.$Post("/Complaint/CreateOrEdit", this.complaintForm);
                        
                        if (Success) {
                            this.$message.success('投诉提交成功！');
                            this.resetComplaintForm();
                            // 刷新投诉记录列表
                            this.$refs.PaginationTableId.Reload();
                        } else {
                            this.$message.error(Message || '提交失败，请稍后重试');
                        }
                    } catch (error) {
                        console.error('提交投诉失败:', error);
                        this.$message.error('提交失败，请稍后重试');
                    } finally {
                        this.submitting = false;
                    }
                } else {
                    this.$message.warning('请填写完整的投诉信息');
                    return false;
                }
            });
        },
        
        // 重置投诉表单
        resetComplaintForm() {
            this.$refs.complaintFormRef.resetFields();
            this.complaintForm.UserId = store.getters.UserId;
        },
        
        // 刷新投诉记录
        refreshComplaints() {
            this.$refs.PaginationTableId.Reload();
        },
        
        
        // 显示投诉详情
        showDetail(row) {
            this.currentComplaint = { ...row };
            this.detailDialogVisible = true;
        },
        
        // 文本截断
        truncateText(text, length) {
            if (!text) return '无';
            if (text.length <= length) return text;
            return text.substring(0, length) + '...';
        },
        
    },
};
</script>

<style scoped>
.app-container {
    padding: 20px;
}

.margin-top-lg {
    margin-top: 20px;
}

.card-title {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
}

.tb-body {
    padding: 10px 0;
}

.table-summary {
    padding: 8px 0;
    font-size: 14px;
    color: #606266;
}

.highlight {
    color: #409EFF;
    font-weight: bold;
}

.complaint-detail {
    padding: 0 10px;
}

.detail-section {
    display: flex;
    margin-bottom: 16px;
    line-height: 1.5;
}

.detail-label {
    width: 100px;
    font-weight: bold;
    color: #606266;
    flex-shrink: 0;
}

.detail-value {
    flex: 1;
    color: #303133;
    word-break: break-word;
}

.detail-content {
    background-color: #f5f7fa;
    padding: 10px;
    border-radius: 4px;
    border-left: 3px solid #409EFF;
}

.clearfix:before,
.clearfix:after {
    display: table;
    content: "";
}

.clearfix:after {
    clear: both;
}

.box-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-page-header {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>