package csu.yulin.model.entity;

import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.abi.datatypes.Event;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 智能合约
 *
 * @author lp
 * @create 2025-03-26
 */
public class CharityDonation extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50600060058190555060006006819055506000600781905550610d6f806100386000396000f3006080604052600436106100e6576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806302da667b146100eb5780630ccc9c30146101225780630cfa2fd8146101a4578063107046bd146102095780632abfab4d1461025f578063312c06121461028a57806336fbad26146102b55780633e97b43f146102e057806386302d8414610345578063ba940fd61461038a578063c56f3cac1461040c578063ef07a81f14610457578063ef88f48b146104b4578063f0f3f2c8146104f5578063f8626af81461054b578063fd1a71ca146105a8575b600080fd5b3480156100f757600080fd5b5061012060048036038101908080359060200190929190803590602001909291905050506105f3565b005b34801561012e57600080fd5b5061014d600480360381019080803590602001909291905050506106b6565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b83811015610190578082015181840152602081019050610175565b505050509050019250505060405180910390f35b3480156101b057600080fd5b506101cf60048036038101908080359060200190929190505050610721565b6040518086815260200185815260200184815260200183600019166000191681526020018281526020019550505050505060405180910390f35b34801561021557600080fd5b50610234600480360381019080803590602001909291905050506107b9565b6040518085815260200184815260200183815260200182815260200194505050505060405180910390f35b34801561026b57600080fd5b506102746107e9565b6040518082815260200191505060405180910390f35b34801561029657600080fd5b5061029f6107ef565b6040518082815260200191505060405180910390f35b3480156102c157600080fd5b506102ca6107f5565b6040518082815260200191505060405180910390f35b3480156102ec57600080fd5b5061030b600480360381019080803590602001909291905050506107fb565b6040518086815260200185815260200184815260200183600019166000191681526020018281526020019550505050505060405180910390f35b34801561035157600080fd5b5061038860048036038101908080359060200190929190803590602001909291908035600019169060200190929190505050610831565b005b34801561039657600080fd5b506103b560048036038101908080359060200190929190505050610966565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b838110156103f85780820151818401526020810190506103dd565b505050509050019250505060405180910390f35b34801561041857600080fd5b5061044160048036038101908080359060200190929190803590602001909291905050506109d1565b6040518082815260200191505060405180910390f35b34801561046357600080fd5b5061048260048036038101908080359060200190929190505050610a01565b604051808681526020018581526020018481526020018381526020018281526020019550505050505060405180910390f35b3480156104c057600080fd5b506104f3600480360381019080803590602001909291908035906020019092919080359060200190929190505050610a91565b005b34801561050157600080fd5b5061052060048036038101908080359060200190929190505050610bd7565b6040518085815260200184815260200183815260200182815260200194505050505060405180910390f35b34801561055757600080fd5b5061057660048036038101908080359060200190929190505050610c51565b604051808681526020018581526020018481526020018381526020018281526020019550505050505060405180910390f35b3480156105b457600080fd5b506105dd6004803603810190808035906020019092919080359060200190929190505050610c87565b6040518082815260200191505060405180910390f35b600560008154809291906001019190505550608060405190810160405280600554815260200183815260200182815260200160008152506000806005548152602001908152602001600020600082015181600001556020820151816001015560408201518160020155606082015181600301559050507f45777831981be495bb6dd356533f1d2444bb6f8bee0a64ad7f2055385c6d2921600554838360405180848152602001838152602001828152602001935050505060405180910390a15050565b60606002600083815260200190815260200160002080548060200260200160405190810160405280929190818152602001828054801561071557602002820191906000526020600020905b815481526020019060010190808311610701575b50505050509050919050565b6000806000806000610731610cb7565b6003600088815260200190815260200160002060a06040519081016040529081600082015481526020016001820154815260200160028201548152602001600382015460001916600019168152602001600482015481525050905080600001518160200151826040015183606001518460800151955095509550955095505091939590929450565b60006020528060005260406000206000915090508060000154908060010154908060020154908060030154905084565b60065481565b60075481565b60055481565b60036020528060005260406000206000915090508060000154908060010154908060020154908060030154908060040154905085565b60076000815480929190600101919050555060a0604051908101604052806007548152602001848152602001838152602001826000191681526020014281525060036000600754815260200190815260200160002060008201518160000155602082015181600101556040820151816002015560608201518160030190600019169055608082015181600401559050506004600084815260200190815260200160002060075490806001815401808255809150509060018203906000526020600020016000909192909190915055507f95095475e5faf0238ac9f35270b2f1e8031198478263783bcbab998d4a0fc604600754848484426040518086815260200185815260200184815260200183600019166000191681526020018281526020019550505050505060405180910390a1505050565b6060600460008381526020019081526020016000208054806020026020016040519081016040528092919081815260200182805480156109c557602002820191906000526020600020905b8154815260200190600101908083116109b1575b50505050509050919050565b6002602052816000526040600020818154811015156109ec57fe5b90600052602060002001600091509150505481565b6000806000806000610a11610cea565b6001600088815260200190815260200160002060a0604051908101604052908160008201548152602001600182015481526020016002820154815260200160038201548152602001600482015481525050905080600001518160200151826040015183606001518460800151955095509550955095505091939590929450565b60066000815480929190600101919050555060a060405190810160405280600654815260200184815260200183815260200182815260200142815250600160006006548152602001908152602001600020600082015181600001556020820151816001015560408201518160020155606082015181600301556080820151816004015590505080600080848152602001908152602001600020600301600082825401925050819055506002600083815260200190815260200160002060065490806001815401808255809150509060018203906000526020600020016000909192909190915055507f5b197864b1e0d4d65361bbf5b9b57c4e7d5c9372e5bc2371c513667c3a97afce60065484848442604051808681526020018581526020018481526020018381526020018281526020019550505050505060405180910390a1505050565b600080600080610be5610d1a565b60008087815260200190815260200160002060806040519081016040529081600082015481526020016001820154815260200160028201548152602001600382015481525050905080600001518160200151826040015183606001519450945094509450509193509193565b60016020528060005260406000206000915090508060000154908060010154908060020154908060030154908060040154905085565b600460205281600052604060002081815481101515610ca257fe5b90600052602060002001600091509150505481565b60a06040519081016040528060008152602001600081526020016000815260200160008019168152602001600081525090565b60a06040519081016040528060008152602001600081526020016000815260200160008152602001600081525090565b6080604051908101604052806000815260200160008152602001600081526020016000815250905600a165627a7a7230582045b68f97fb223c8f7dfb1724c3d899e6db5783b88e1eeb0683dab1db1ca26ca30029"};

    public static final String BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"constant\":false,\"inputs\":[{\"name\":\"orgId\",\"type\":\"uint256\"},{\"name\":\"targetAmount\",\"type\":\"uint256\"}],\"name\":\"createProject\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"}],\"name\":\"getProjectDonations\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"voucherId\",\"type\":\"uint256\"}],\"name\":\"getVoucher\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"bytes32\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"projects\",\"outputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"orgId\",\"type\":\"uint256\"},{\"name\":\"targetAmount\",\"type\":\"uint256\"},{\"name\":\"raisedAmount\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"donationCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"voucherCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"projectCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"vouchers\",\"outputs\":[{\"name\":\"voucherId\",\"type\":\"uint256\"},{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"orgId\",\"type\":\"uint256\"},{\"name\":\"ipfsHash\",\"type\":\"bytes32\"},{\"name\":\"timestamp\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"orgId\",\"type\":\"uint256\"},{\"name\":\"ipfsHash\",\"type\":\"bytes32\"}],\"name\":\"uploadVoucher\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"}],\"name\":\"getProjectVouchers\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"projectDonations\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"donationId\",\"type\":\"uint256\"}],\"name\":\"getDonation\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"userId\",\"type\":\"uint256\"},{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"donate\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"}],\"name\":\"getProject\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"donations\",\"outputs\":[{\"name\":\"donationId\",\"type\":\"uint256\"},{\"name\":\"userId\",\"type\":\"uint256\"},{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"amount\",\"type\":\"uint256\"},{\"name\":\"timestamp\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"projectVouchers\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"projectId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"orgId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"targetAmount\",\"type\":\"uint256\"}],\"name\":\"ProjectCreated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"donationId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"userId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"projectId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"timestamp\",\"type\":\"uint256\"}],\"name\":\"Donated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"voucherId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"projectId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"orgId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"ipfsHash\",\"type\":\"bytes32\"},{\"indexed\":false,\"name\":\"timestamp\",\"type\":\"uint256\"}],\"name\":\"VoucherUploaded\",\"type\":\"event\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_CREATEPROJECT = "createProject";

    public static final String FUNC_GETPROJECTDONATIONS = "getProjectDonations";

    public static final String FUNC_GETVOUCHER = "getVoucher";

    public static final String FUNC_PROJECTS = "projects";

    public static final String FUNC_DONATIONCOUNT = "donationCount";

    public static final String FUNC_VOUCHERCOUNT = "voucherCount";

    public static final String FUNC_PROJECTCOUNT = "projectCount";

    public static final String FUNC_VOUCHERS = "vouchers";

    public static final String FUNC_UPLOADVOUCHER = "uploadVoucher";

    public static final String FUNC_GETPROJECTVOUCHERS = "getProjectVouchers";

    public static final String FUNC_PROJECTDONATIONS = "projectDonations";

    public static final String FUNC_GETDONATION = "getDonation";

    public static final String FUNC_DONATE = "donate";

    public static final String FUNC_GETPROJECT = "getProject";

    public static final String FUNC_DONATIONS = "donations";

    public static final String FUNC_PROJECTVOUCHERS = "projectVouchers";

    public static final Event PROJECTCREATED_EVENT = new Event("ProjectCreated",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event DONATED_EVENT = new Event("Donated",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event VOUCHERUPLOADED_EVENT = new Event("VoucherUploaded",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Bytes32>() {
            }, new TypeReference<Uint256>() {
            }));

    protected CharityDonation(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static CharityDonation load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new CharityDonation(contractAddress, client, credential);
    }

    public static CharityDonation deploy(Client client, CryptoKeyPair credential) throws ContractException {
        return deploy(CharityDonation.class, client, credential, getBinary(client.getCryptoSuite()), "");
    }

    public TransactionReceipt createProject(BigInteger orgId, BigInteger targetAmount) {
        final Function function = new Function(
                FUNC_CREATEPROJECT,
                Arrays.asList(new Uint256(orgId),
                        new Uint256(targetAmount)),
                Collections.emptyList());
        return executeTransaction(function);
    }

    public byte[] createProject(BigInteger orgId, BigInteger targetAmount, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_CREATEPROJECT,
                Arrays.asList(new Uint256(orgId),
                        new Uint256(targetAmount)),
                Collections.emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateProject(BigInteger orgId, BigInteger targetAmount) {
        final Function function = new Function(
                FUNC_CREATEPROJECT,
                Arrays.asList(new Uint256(orgId),
                        new Uint256(targetAmount)),
                Collections.emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<BigInteger, BigInteger> getCreateProjectInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_CREATEPROJECT,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue()
        );
    }

    public List getProjectDonations(BigInteger projectId) throws ContractException {
        final Function function = new Function(FUNC_GETPROJECTDONATIONS,
                Collections.singletonList(new Uint256(projectId)),
                Collections.singletonList(new TypeReference<DynamicArray<Uint256>>() {
                }));
        List<Type> result = (List<Type>) executeCallWithSingleValueReturn(function, List.class);
        return convertToNative(result);
    }

    public Tuple5<BigInteger, BigInteger, BigInteger, byte[], BigInteger> getVoucher(BigInteger voucherId) throws ContractException {
        final Function function = new Function(FUNC_GETVOUCHER,
                Collections.singletonList(new Uint256(voucherId)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Bytes32>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<BigInteger, BigInteger, BigInteger, byte[], BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (byte[]) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public Tuple4<BigInteger, BigInteger, BigInteger, BigInteger> projects(BigInteger param0) throws ContractException {
        final Function function = new Function(FUNC_PROJECTS,
                Collections.singletonList(new Uint256(param0)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue());
    }

    public BigInteger donationCount() throws ContractException {
        final Function function = new Function(FUNC_DONATIONCOUNT,
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger voucherCount() throws ContractException {
        final Function function = new Function(FUNC_VOUCHERCOUNT,
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger projectCount() throws ContractException {
        final Function function = new Function(FUNC_PROJECTCOUNT,
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Tuple5<BigInteger, BigInteger, BigInteger, byte[], BigInteger> vouchers(BigInteger param0) throws ContractException {
        final Function function = new Function(FUNC_VOUCHERS,
                Collections.singletonList(new Uint256(param0)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Bytes32>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<BigInteger, BigInteger, BigInteger, byte[], BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (byte[]) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public TransactionReceipt uploadVoucher(BigInteger projectId, BigInteger orgId, byte[] ipfsHash) {
        final Function function = new Function(
                FUNC_UPLOADVOUCHER,
                Arrays.asList(new Uint256(projectId),
                        new Uint256(orgId),
                        new Bytes32(ipfsHash)),
                Collections.emptyList());
        return executeTransaction(function);
    }

    public byte[] uploadVoucher(BigInteger projectId, BigInteger orgId, byte[] ipfsHash, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_UPLOADVOUCHER,
                Arrays.asList(new Uint256(projectId),
                        new Uint256(orgId),
                        new Bytes32(ipfsHash)),
                Collections.emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUploadVoucher(BigInteger projectId, BigInteger orgId, byte[] ipfsHash) {
        final Function function = new Function(
                FUNC_UPLOADVOUCHER,
                Arrays.asList(new Uint256(projectId),
                        new Uint256(orgId),
                        new Bytes32(ipfsHash)),
                Collections.emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<BigInteger, BigInteger, byte[]> getUploadVoucherInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_UPLOADVOUCHER,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Bytes32>() {
                }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, BigInteger, byte[]>(

                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (byte[]) results.get(2).getValue()
        );
    }

    public List getProjectVouchers(BigInteger projectId) throws ContractException {
        final Function function = new Function(FUNC_GETPROJECTVOUCHERS,
                Collections.singletonList(new Uint256(projectId)),
                Collections.singletonList(new TypeReference<DynamicArray<Uint256>>() {
                }));
        List<Type> result = (List<Type>) executeCallWithSingleValueReturn(function, List.class);
        return convertToNative(result);
    }

    public BigInteger projectDonations(BigInteger param0, BigInteger param1) throws ContractException {
        final Function function = new Function(FUNC_PROJECTDONATIONS,
                Arrays.asList(new Uint256(param0),
                        new Uint256(param1)),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> getDonation(BigInteger donationId) throws ContractException {
        final Function function = new Function(FUNC_GETDONATION,
                Collections.singletonList(new Uint256(donationId)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public TransactionReceipt donate(BigInteger userId, BigInteger projectId, BigInteger amount) {
        final Function function = new Function(
                FUNC_DONATE,
                Arrays.asList(new Uint256(userId),
                        new Uint256(projectId),
                        new Uint256(amount)),
                Collections.emptyList());
        return executeTransaction(function);
    }

    public byte[] donate(BigInteger userId, BigInteger projectId, BigInteger amount, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_DONATE,
                Arrays.asList(new Uint256(userId),
                        new Uint256(projectId),
                        new Uint256(amount)),
                Collections.emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForDonate(BigInteger userId, BigInteger projectId, BigInteger amount) {
        final Function function = new Function(
                FUNC_DONATE,
                Arrays.asList(new Uint256(userId),
                        new Uint256(projectId),
                        new Uint256(amount)),
                Collections.emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<BigInteger, BigInteger, BigInteger> getDonateInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_DONATE,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue()
        );
    }

    public Tuple4<BigInteger, BigInteger, BigInteger, BigInteger> getProject(BigInteger projectId) throws ContractException {
        final Function function = new Function(FUNC_GETPROJECT,
                Collections.singletonList(new Uint256(projectId)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue());
    }

    public Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> donations(BigInteger param0) throws ContractException {
        final Function function = new Function(FUNC_DONATIONS,
                Collections.singletonList(new Uint256(param0)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public BigInteger projectVouchers(BigInteger param0, BigInteger param1) throws ContractException {
        final Function function = new Function(FUNC_PROJECTVOUCHERS,
                Arrays.asList(new Uint256(param0),
                        new Uint256(param1)),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public List<ProjectCreatedEventResponse> getProjectCreatedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(PROJECTCREATED_EVENT, transactionReceipt);
        ArrayList<ProjectCreatedEventResponse> responses = new ArrayList<ProjectCreatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ProjectCreatedEventResponse typedResponse = new ProjectCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.projectId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.orgId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.targetAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeProjectCreatedEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(PROJECTCREATED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeProjectCreatedEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(PROJECTCREATED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public List<DonatedEventResponse> getDonatedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(DONATED_EVENT, transactionReceipt);
        ArrayList<DonatedEventResponse> responses = new ArrayList<DonatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            DonatedEventResponse typedResponse = new DonatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.donationId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.userId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.projectId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeDonatedEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(DONATED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeDonatedEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(DONATED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public List<VoucherUploadedEventResponse> getVoucherUploadedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(VOUCHERUPLOADED_EVENT, transactionReceipt);
        ArrayList<VoucherUploadedEventResponse> responses = new ArrayList<VoucherUploadedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            VoucherUploadedEventResponse typedResponse = new VoucherUploadedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.voucherId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.projectId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.orgId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.ipfsHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeVoucherUploadedEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(VOUCHERUPLOADED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeVoucherUploadedEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(VOUCHERUPLOADED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public static class ProjectCreatedEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger projectId;

        public BigInteger orgId;

        public BigInteger targetAmount;
    }

    public static class DonatedEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger donationId;

        public BigInteger userId;

        public BigInteger projectId;

        public BigInteger amount;

        public BigInteger timestamp;
    }

    public static class VoucherUploadedEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger voucherId;

        public BigInteger projectId;

        public BigInteger orgId;

        public byte[] ipfsHash;

        public BigInteger timestamp;
    }
}
